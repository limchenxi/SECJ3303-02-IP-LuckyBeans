package com.example.mentalhealth.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mentalhealth.model.AssessmentOption;
import com.example.mentalhealth.model.AssessmentQuestion;
import com.example.mentalhealth.model.Option;
import com.example.mentalhealth.model.Question;
import com.example.mentalhealth.model.QuestionForm;
import com.example.mentalhealth.model.UserAssessment;
import com.example.mentalhealth.model.UserAssessmentAnswer;
import com.example.mentalhealth.model.UserProgress;
import com.example.mentalhealth.model.UserStudent;
import com.example.mentalhealth.repository.AssessmentOptionRepository;
import com.example.mentalhealth.repository.AssessmentQuestionRepository;
import com.example.mentalhealth.repository.UserAssessmentAnswerRepository;
import com.example.mentalhealth.repository.UserAssessmentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/symptoms")
public class SymptomsController {

    @Autowired
    private AssessmentQuestionRepository questionRepository;

    @Autowired
    private AssessmentOptionRepository optionRepository;

    @Autowired
    private UserAssessmentRepository assessmentRepository;

    @Autowired
    private UserAssessmentAnswerRepository answerRepository;

    @GetMapping
    public String symptoms(HttpSession session, Model model) {
        Integer userId = getOrCreateUserId(session);

        UserStudent user = new UserStudent();
        user.setUserId(userId);
        user.setFullName("John Smith");
        user.setInitial("J");
        model.addAttribute("user", user);

        UserProgress userProgress = new UserProgress("7-day self-care streak!");
        model.addAttribute("userProgress", userProgress);

        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");
        if (currentQuestion == null) {
            currentQuestion = 1;
            session.setAttribute("currentQuestion", currentQuestion);
        }

        return showQuestion(session, model, userId);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer userId = getOrCreateUserId(session);

        UserStudent user = new UserStudent();
        user.setUserId(userId);
        user.setFullName("John Smith");
        user.setInitial("J");
        model.addAttribute("user", user);

        UserProgress userProgress = new UserProgress("7-day self-care streak!");
        model.addAttribute("userProgress", userProgress);

        return showDashboard(userId, model);
    }

    private String showQuestion(HttpSession session, Model model, Integer userId) {
        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");
        if (currentQuestion == null) {
            currentQuestion = 1;
            session.setAttribute("currentQuestion", currentQuestion);
        }

        List<AssessmentQuestion> allQuestions = questionRepository.findAllByOrderByQuestionOrderAsc();
        int totalQuestions = allQuestions.size();

        if (currentQuestion > totalQuestions || currentQuestion < 1) {
            return "redirect:/symptoms/dashboard";
        }

        AssessmentQuestion assessmentQuestion = allQuestions.get(currentQuestion - 1);

        List<Option> options = new ArrayList<>();
        int index = 0;
        for (AssessmentOption ao : assessmentQuestion.getOptions()) {
            options.add(new Option(index, ao.getOptionText()));
            index++;
        }

        Integer selectedAnswer = (Integer) session.getAttribute("answer_" + currentQuestion);
        int progressPercentage = (currentQuestion * 100) / totalQuestions;

        Question questionDto = new Question();
        questionDto.setId(assessmentQuestion.getQuestionId());
        questionDto.setText(assessmentQuestion.getQuestionText());
        questionDto.setOptions(options);

        model.addAttribute("currentQuestion", currentQuestion);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("progressPercentage", progressPercentage);
        model.addAttribute("question", questionDto);
        model.addAttribute("selectedAnswer", selectedAnswer);
        model.addAttribute("questionForm", new QuestionForm());

        List<UserAssessment> assessments = assessmentRepository
            .findByUserIdOrderByAssessmentDateDesc(userId);

        if (!assessments.isEmpty()) {
            UserAssessment latest = assessments.get(0);
            model.addAttribute("latestScore", latest.getTotalScore());
            model.addAttribute("latestAssessment", latest.getAssessmentResult());
            model.addAttribute("latestSeverity", latest.getSeverityLevel());
        }

        model.addAttribute("assessmentHistory", assessments);

        return "symptoms-recognition/symptoms-recognition";
    }

    private String showDashboard(Integer userId, Model model) {
        List<UserAssessment> assessments = assessmentRepository
            .findByUserIdOrderByAssessmentDateDesc(userId);

        if (!assessments.isEmpty()) {
            UserAssessment latest = assessments.get(0);
            model.addAttribute("totalScore", latest.getTotalScore());
            model.addAttribute("assessment", latest.getAssessmentResult());
            model.addAttribute("severityLevel", latest.getSeverityLevel());
        }

        model.addAttribute("assessmentHistory", assessments);

        return "symptoms-recognition/symptoms-recognition";
    }

    @PostMapping("/submit")
    public String submitAnswer(@ModelAttribute QuestionForm questionForm,
                               HttpSession session) {
        Integer userId = getOrCreateUserId(session);
        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");

        if (currentQuestion == null) {
            currentQuestion = 1;
        }

        session.setAttribute("answer_" + currentQuestion, questionForm.getAnswer());
        session.setAttribute("questionId_" + currentQuestion, questionForm.getQuestionId());

        int totalQuestions = questionRepository.findAllByOrderByQuestionOrderAsc().size();

        if (currentQuestion < totalQuestions) {
            session.setAttribute("currentQuestion", currentQuestion + 1);
            return "redirect:/symptoms";
        } else {
            saveAssessmentToDatabase(userId, session, totalQuestions);
            for (int i = 1; i <= totalQuestions; i++) {
                session.removeAttribute("answer_" + i);
                session.removeAttribute("questionId_" + i);
            }
            session.removeAttribute("currentQuestion");
            return "redirect:/symptoms/dashboard";
        }
    }

    @GetMapping("/previous")
    public String previousQuestion(HttpSession session) {
        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");
        if (currentQuestion != null && currentQuestion > 1) {
            session.setAttribute("currentQuestion", currentQuestion - 1);
        }
        return "redirect:/symptoms";
    }

    @GetMapping("/start")
    public String startAssessment(HttpSession session) {
        session.setAttribute("currentQuestion", 1);
        return "redirect:/symptoms";
    }

    @GetMapping("/restart")
    public String restartAssessment(HttpSession session) {
        int totalQuestions = questionRepository.findAllByOrderByQuestionOrderAsc().size();
        for (int i = 1; i <= totalQuestions; i++) {
            session.removeAttribute("answer_" + i);
            session.removeAttribute("questionId_" + i);
        }
        session.setAttribute("currentQuestion", 1);
        return "redirect:/symptoms";
    }

    private void saveAssessmentToDatabase(Integer userId, HttpSession session, int totalQuestions) {
        int totalScore = 0;
        List<Integer> answers = new ArrayList<>();
        List<Integer> questionIds = new ArrayList<>();

        for (int i = 1; i <= totalQuestions; i++) {
            Integer answerIndex = (Integer) session.getAttribute("answer_" + i);
            Integer questionId = (Integer) session.getAttribute("questionId_" + i);

            if (answerIndex != null && questionId != null) {
                AssessmentQuestion question = questionRepository.findById(questionId).orElse(null);
                if (question != null) {
                    List<AssessmentOption> optionList = question.getOptions();
                    int value = answerIndex;

                    answers.add(value);
                    questionIds.add(questionId);
                    totalScore += value;
                }
            }
        }

        String result = analyseAssessment(totalScore);
        String severityLevel = getSeverityLevel(totalScore);

        UserAssessment assessment = new UserAssessment();
        assessment.setUserId(userId);
        assessment.setTotalScore(totalScore);
        assessment.setAssessmentResult(result);
        assessment.setSeverityLevel(severityLevel);
        assessment.setAssessmentDate(LocalDateTime.now());

        UserAssessment savedAssessment = assessmentRepository.save(assessment);

        for (int i = 0; i < answers.size(); i++) {
            UserAssessmentAnswer answer = new UserAssessmentAnswer();
            answer.setAssessmentId(savedAssessment.getAssessmentId());
            answer.setQuestionId(questionIds.get(i));
            answer.setSelectedOptionId(answers.get(i) + 1);
            answer.setAnswerValue(answers.get(i));
            answerRepository.save(answer);
        }
    }

    private String analyseAssessment(int score) {
        if (score <= 4) return "Minimal or no symptoms. You're doing well!";
        else if (score <= 9) return "Mild symptoms. Consider some self-care activities.";
        else if (score <= 14) return "Moderate symptoms. We recommend speaking with a counselor.";
        else return "Severe symptoms. Please seek professional help immediately.";
    }

    private String getSeverityLevel(int score) {
        if (score <= 4) return "minimal";
        else if (score <= 9) return "mild";
        else if (score <= 14) return "moderate";
        else return "severe";
    }

    private Integer getOrCreateUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            userId = 1;
            session.setAttribute("userId", userId);
        }
        return userId;
    }
}
