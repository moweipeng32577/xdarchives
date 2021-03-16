package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Szh_media_metadata {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  private String id;
  private String batchcode;
  private String mediaid;
  private String archivecode;
  private String filename;
  private String scanpagecode;
  private String qbcode;
  private String filepath;
  private String digitaltime;
  private String digitalobjdescribe;
  private String describeaccreditdescribe;
  private String formatname;
  private String formatversion;
  private String colorspace;
  private String reduceplan;
  private String reduceratio;
  private Integer levelresolution;
  private Integer verticalresolution;
  private String equipmenttype;
  private String equipmentmanufacturer;
  private String equipmentmodel;
  private String equipmentsensitization;
  private String digitalsoftname;
  private String digitalsoftversion;
  private String digitalsoftvendor;
  private String readsoftcondition;
  private String digitalresultsturnmsg;
  private Integer picturewidth;
  private Integer pictureheight;
  private Integer bitdepth;
  private String copyright;
  private Integer filesize;
  private String md5;

  public Szh_media_metadata(){}

  public Szh_media_metadata(String batchcode,String mediaid,String archivecode,String filename,String filepath,Integer filesize){
    this.batchcode = batchcode;
    this.mediaid = mediaid;
    this.archivecode = archivecode;
    this.filename = filename;
    this.filepath = filepath;
    this.filesize = filesize;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBatchcode() {
    return batchcode;
  }

  public void setBatchcode(String batchcode) {
    this.batchcode = batchcode;
  }

  public String getMediaid() {
    return mediaid;
  }

  public void setMediaid(String mediaid) {
    this.mediaid = mediaid;
  }

  public String getArchivecode() {
    return archivecode;
  }

  public void setArchivecode(String archivecode) {
    this.archivecode = archivecode;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getScanpagecode() {
    return scanpagecode;
  }

  public void setScanpagecode(String scanpagecode) {
    this.scanpagecode = scanpagecode;
  }

  public String getQbcode() {
    return qbcode;
  }

  public void setQbcode(String qbcode) {
    this.qbcode = qbcode;
  }

  public String getFilepath() {
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }

  public String getDigitaltime() {
    return digitaltime;
  }

  public void setDigitaltime(String digitaltime) {
    this.digitaltime = digitaltime;
  }

  public String getDigitalobjdescribe() {
    return digitalobjdescribe;
  }

  public void setDigitalobjdescribe(String digitalobjdescribe) {
    this.digitalobjdescribe = digitalobjdescribe;
  }

  public String getDescribeaccreditdescribe() {
    return describeaccreditdescribe;
  }

  public void setDescribeaccreditdescribe(String describeaccreditdescribe) {
    this.describeaccreditdescribe = describeaccreditdescribe;
  }

  public String getFormatname() {
    return formatname;
  }

  public void setFormatname(String formatname) {
    this.formatname = formatname;
  }

  public String getFormatversion() {
    return formatversion;
  }

  public void setFormatversion(String formatversion) {
    this.formatversion = formatversion;
  }

  public String getColorspace() {
    return colorspace;
  }

  public void setColorspace(String colorspace) {
    this.colorspace = colorspace;
  }

  public String getReduceplan() {
    return reduceplan;
  }

  public void setReduceplan(String reduceplan) {
    this.reduceplan = reduceplan;
  }

  public String getReduceratio() {
    return reduceratio;
  }

  public void setReduceratio(String reduceratio) {
    this.reduceratio = reduceratio;
  }

  public Integer getLevelresolution() {
    return levelresolution;
  }

  public void setLevelresolution(Integer levelresolution) {
    this.levelresolution = levelresolution;
  }

  public Integer getVerticalresolution() {
    return verticalresolution;
  }

  public void setVerticalresolution(Integer verticalresolution) {
    this.verticalresolution = verticalresolution;
  }

  public String getEquipmenttype() {
    return equipmenttype;
  }

  public void setEquipmenttype(String equipmenttype) {
    this.equipmenttype = equipmenttype;
  }

  public String getEquipmentmanufacturer() {
    return equipmentmanufacturer;
  }

  public void setEquipmentmanufacturer(String equipmentmanufacturer) {
    this.equipmentmanufacturer = equipmentmanufacturer;
  }

  public String getEquipmentmodel() {
    return equipmentmodel;
  }

  public void setEquipmentmodel(String equipmentmodel) {
    this.equipmentmodel = equipmentmodel;
  }

  public String getEquipmentsensitization() {
    return equipmentsensitization;
  }

  public void setEquipmentsensitization(String equipmentsensitization) {
    this.equipmentsensitization = equipmentsensitization;
  }

  public String getDigitalsoftname() {
    return digitalsoftname;
  }

  public void setDigitalsoftname(String digitalsoftname) {
    this.digitalsoftname = digitalsoftname;
  }

  public String getDigitalsoftversion() {
    return digitalsoftversion;
  }

  public void setDigitalsoftversion(String digitalsoftversion) {
    this.digitalsoftversion = digitalsoftversion;
  }

  public String getDigitalsoftvendor() {
    return digitalsoftvendor;
  }

  public void setDigitalsoftvendor(String digitalsoftvendor) {
    this.digitalsoftvendor = digitalsoftvendor;
  }

  public String getReadsoftcondition() {
    return readsoftcondition;
  }

  public void setReadsoftcondition(String readsoftcondition) {
    this.readsoftcondition = readsoftcondition;
  }

  public String getDigitalresultsturnmsg() {
    return digitalresultsturnmsg;
  }

  public void setDigitalresultsturnmsg(String digitalresultsturnmsg) {
    this.digitalresultsturnmsg = digitalresultsturnmsg;
  }

  public Integer getPicturewidth() {
    return picturewidth;
  }

  public void setPicturewidth(Integer picturewidth) {
    this.picturewidth = picturewidth;
  }

  public Integer getPictureheight() {
    return pictureheight;
  }

  public void setPictureheight(Integer pictureheight) {
    this.pictureheight = pictureheight;
  }

  public Integer getBitdepth() {
    return bitdepth;
  }

  public void setBitdepth(Integer bitdepth) {
    this.bitdepth = bitdepth;
  }

  public String getCopyright() {
    return copyright;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  public Integer getFilesize() {
    return filesize;
  }

  public void setFilesize(Integer filesize) {
    this.filesize = filesize;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }
}
