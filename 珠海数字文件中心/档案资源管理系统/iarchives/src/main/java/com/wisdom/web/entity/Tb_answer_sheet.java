package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
/**答卷信息*/
public class Tb_answer_sheet {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String answersheetID;//答卷ID

    @Column(columnDefinition = "char(36)")
    private String questionnaireID;//问卷ID

    @Column(columnDefinition = "varchar(30)")
    private String name;//姓名

    @Column(columnDefinition = "varchar(20)")
    private String createtime;//答题时间

    @Column(columnDefinition = "varchar(50)")
    private String phone;//电话

    @Column(columnDefinition = "varchar(50)")
    private String IDcard;//身份证号码或者答题者用户id

    public Tb_answer_sheet() {
    }

    public Tb_answer_sheet(String questionnaireID, String name, String createtime, String phone, String IDcard) {
        this.questionnaireID = questionnaireID;
        this.name = name;
        this.createtime = createtime;
        this.phone = phone;
        this.IDcard = IDcard;
    }

    public String getAnswerSheetID() {
        return answersheetID;
    }

    public void setAnswerSheetID(String answersheetID) {
        this.answersheetID = answersheetID;
    }

    public String getQuestionnaireID() {
        return questionnaireID;
    }

    public void setQuestionnaireID(String questionnaireID) {
        this.questionnaireID = questionnaireID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIDcard() {
        return IDcard;
    }

    public void setIDcard(String IDcard) {
        this.IDcard = IDcard;
    }

    @Override
    public String toString() {
        return "Tb_answer_sheet{" +
                "answersheetID='" + answersheetID + '\'' +
                ", questionnaireID='" + questionnaireID + '\'' +
                ", name='" + name + '\'' +
                ", createtime='" + createtime + '\'' +
                ", phone='" + phone + '\'' +
                ", IDcard='" + IDcard + '\'' +
                '}';
    }
}
