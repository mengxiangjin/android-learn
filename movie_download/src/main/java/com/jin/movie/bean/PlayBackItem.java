package com.jin.movie.bean;

import java.util.List;

public class PlayBackItem {
    private int id;
    private int anchorUserId;
    private String nickName;
    private String userLogo;
    private String userSlogan;
    private int buyStatus;
    private int videoCoin;
    private int videoSort;
    private long isRecommend;
    private int globalRecommendSort;
    private String liveId;
    private String fileId;
    private String videoTitle;
    private String title;
    private String converImage;
    private String videoUrl;
    private String s3VideoUrl;
    private String startTime;
    private String endTime;
    private int durationsTime;
    private int videoViews;
    private long videoSize;
    private int videoStatus;
    private int s3Status;
    private String addTime;
    private String updatedTime;
    private boolean globalRecommend;


    //自定义属性（非Json映射）
    private int tsFileTotalCounts;
    private List<TSBean> tsBeanList;


    public int getTsFileTotalCounts() {
        return tsFileTotalCounts;
    }

    public void setTsFileTotalCounts(int tsFileTotalCounts) {
        this.tsFileTotalCounts = tsFileTotalCounts;
    }

    public List<TSBean> getTsBeanList() {
        return tsBeanList;
    }

    public void setTsBeanList(List<TSBean> tsBeanList) {
        this.tsBeanList = tsBeanList;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnchorUserId() {
        return anchorUserId;
    }

    public void setAnchorUserId(int anchorUserId) {
        this.anchorUserId = anchorUserId;
    }

    public String getNickName() {
        return nickName.replace(" ","");
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public String getUserSlogan() {
        return userSlogan;
    }

    public void setUserSlogan(String userSlogan) {
        this.userSlogan = userSlogan;
    }

    public int getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(int buyStatus) {
        this.buyStatus = buyStatus;
    }

    public int getVideoCoin() {
        return videoCoin;
    }

    public void setVideoCoin(int videoCoin) {
        this.videoCoin = videoCoin;
    }

    public int getVideoSort() {
        return videoSort;
    }

    public void setVideoSort(int videoSort) {
        this.videoSort = videoSort;
    }

    public long getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(long isRecommend) {
        this.isRecommend = isRecommend;
    }

    public int getGlobalRecommendSort() {
        return globalRecommendSort;
    }

    public void setGlobalRecommendSort(int globalRecommendSort) {
        this.globalRecommendSort = globalRecommendSort;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConverImage() {
        return converImage;
    }

    public void setConverImage(String converImage) {
        this.converImage = converImage;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getS3VideoUrl() {
        return s3VideoUrl;
    }

    public void setS3VideoUrl(String s3VideoUrl) {
        this.s3VideoUrl = s3VideoUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDurationsTime() {
        return durationsTime;
    }

    public void setDurationsTime(int durationsTime) {
        this.durationsTime = durationsTime;
    }

    public int getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(int videoViews) {
        this.videoViews = videoViews;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public int getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(int videoStatus) {
        this.videoStatus = videoStatus;
    }

    public int getS3Status() {
        return s3Status;
    }

    public void setS3Status(int s3Status) {
        this.s3Status = s3Status;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isGlobalRecommend() {
        return globalRecommend;
    }

    public void setGlobalRecommend(boolean globalRecommend) {
        this.globalRecommend = globalRecommend;
    }
}
