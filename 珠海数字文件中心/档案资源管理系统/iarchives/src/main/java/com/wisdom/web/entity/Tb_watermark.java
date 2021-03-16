package com.wisdom.web.entity;

import com.xdtech.project.lot.mjj.message.type.Int;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_watermark {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  private String id;
  private String title;
  private String watermark_picture_name;
  private String watermark_picture_path;
  private String watermark_picture_text;
  private String location;
  private String coordinates;
  private String degree;
  private String transparency;
  private String color;
  private String isrepeat;
  private String iscoordinates;
  private String ispicture;
  private String isdefault;
  private String organid;
  private String namedefault;
  private String useip;   //利用者id
  private String isuse;  //利用平台
  private String ismanage;  //管理平台
  private Integer fontsize;//字体大小
  private Integer linewidth;//字体加粗
  private Integer spacing;//字体间距

  public String getNamedefault() {
    return namedefault;
  }

  public void setNamedefault(String namedefault) {
    this.namedefault = namedefault;
  }

  public String getIsmanage() {
    return ismanage;
  }

  public String getIsuse() {
    return isuse;
  }

  public String getUseip() {
    return useip;
  }

  public void setIsmanage(String ismanage) {
    this.ismanage = ismanage;
  }

  public void setIsuse(String isuse) {
    this.isuse = isuse;
  }

  public void setUseip(String useip) {
    this.useip = useip;
  }

  public String getIscoordinates() {
    return iscoordinates;
  }

  public void setIscoordinates(String iscoordinates) {
    this.iscoordinates = iscoordinates;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getOrganid() {
    return organid;
  }

  public void setOrganid(String organid) {
    this.organid = organid;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getWatermark_picture_name() {
    return watermark_picture_name;
  }

  public void setWatermark_picture_name(String watermark_picture_name) {
    this.watermark_picture_name = watermark_picture_name;
  }

  public String getWatermark_picture_path() {
    return watermark_picture_path;
  }

  public void setWatermark_picture_path(String watermark_picture_path) {
    this.watermark_picture_path = watermark_picture_path;
  }

  public String getWatermark_picture_text() {
    return watermark_picture_text;
  }

  public void setWatermark_picture_text(String watermark_picture_text) {
    this.watermark_picture_text = watermark_picture_text;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(String coordinates) {
    this.coordinates = coordinates;
  }

  public String getDegree() {
    return degree;
  }

  public void setDegree(String degree) {
    this.degree = degree;
  }

  public String getTransparency() {
    return transparency;
  }

  public void setTransparency(String transparency) {
    this.transparency = transparency;
  }

  public String getIspicture() {
    return ispicture;
  }

  public void setIspicture(String ispicture) {
    this.ispicture = ispicture;
  }

  public String getIsdefault() {
    return isdefault;
  }

  public void setIsdefault(String isdefault) {
    this.isdefault = isdefault;
  }

  public String getIsrepeat() {
    return isrepeat;
  }

  public void setIsrepeat(String isrepeat) {
    this.isrepeat = isrepeat;
  }

  public Integer getFontsize() {
    return fontsize;
  }

  public void setFontsize(Integer fontsize) {
    this.fontsize = fontsize;
  }

  public Integer getLinewidth() {
    return linewidth;
  }

  public void setLinewidth(Integer linewidth) {
    this.linewidth = linewidth;
  }

  public Integer getSpacing() {
    return spacing;
  }

  public void setSpacing(Integer spacing) {
    this.spacing = spacing;
  }

  @Override
  public String toString() {
    return "Tb_watermark{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", watermark_picture_name='" + watermark_picture_name + '\'' +
            ", watermark_picture_path='" + watermark_picture_path + '\'' +
            ", watermark_picture_text='" + watermark_picture_text + '\'' +
            ", location='" + location + '\'' +
            ", coordinates='" + coordinates + '\'' +
            ", degree='" + degree + '\'' +
            ", transparency='" + transparency + '\'' +
            ", color='" + color + '\'' +
            ", isrepeat='" + isrepeat + '\'' +
            ", iscoordinates='" + iscoordinates + '\'' +
            ", ispicture='" + ispicture + '\'' +
            ", isdefault='" + isdefault + '\'' +
            ", organid='" + organid + '\'' +
            ", namedefault='" + namedefault + '\'' +
            ", useip='" + useip + '\'' +
            ", isuse='" + isuse + '\'' +
            ", ismanage='" + ismanage + '\'' +
            ", fontsize=" + fontsize +
            ", linewidth=" + linewidth +
            ", spacing=" + spacing +
            '}';
  }
}
