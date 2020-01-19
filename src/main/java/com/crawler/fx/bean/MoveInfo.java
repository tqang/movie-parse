package com.crawler.fx.bean;

import java.util.List;

public class MoveInfo {
    private String title;

    private String studio;

    private String year;

    private String outline;

    private String runtime;

    private String director;

    private List<String> actor;

    private String release;

    private String number;

    private String cover;

    private String serise;

    private String imagecut;

    private List<String> tag;

    private String label;

    private String actor_photo;

    private String website;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getImagecut() {
        return imagecut;
    }

    public void setImagecut(String imagecut) {
        this.imagecut = imagecut;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getActor_photo() {
        return actor_photo;
    }

    public void setActor_photo(String actor_photo) {
        this.actor_photo = actor_photo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSerise() {
        return serise;
    }

    public void setSerise(String serise) {
        this.serise = serise;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public List<String> getActor() {
        return actor;
    }

    public void setActor(List<String> actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        return "MoveInfo{" +
                "title='" + title + '\'' +
                ", studio='" + studio + '\'' +
                ", year='" + year + '\'' +
                ", outline='" + outline + '\'' +
                ", runtime='" + runtime + '\'' +
                ", director='" + director + '\'' +
                ", actor=" + actor +
                ", release='" + release + '\'' +
                ", number='" + number + '\'' +
                ", cover='" + cover + '\'' +
                ", serise='" + serise + '\'' +
                ", imagecut='" + imagecut + '\'' +
                ", tag=" + tag +
                ", label='" + label + '\'' +
                ", actor_photo='" + actor_photo + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
