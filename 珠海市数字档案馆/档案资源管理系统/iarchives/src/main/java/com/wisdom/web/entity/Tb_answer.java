package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_answer {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String answerID;//答卷ID

    @Column(columnDefinition = "char(36)")
    private String answersheetID;//答卷ID

    @Column(columnDefinition = "char(36)")
    private String questionID;//问题ID

    @Column(columnDefinition = "varchar(200)")
    private String answer;//答案

    public Tb_answer() {
    }

    public Tb_answer(String answersheetID, String questionID, String answer) {
        this.answersheetID = answersheetID;
        this.questionID = questionID;
        this.answer = answer;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public String getAnswersheetID() {
        return answersheetID;
    }

    public void setAnswersheetID(String answersheetID) {
        this.answersheetID = answersheetID;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Tb_answer{" +
                "answerID='" + answerID + '\'' +
                ", answersheetID='" + answersheetID + '\'' +
                ", questionID='" + questionID + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
