package edu.asu.cassess.persist.entity.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name="github_weight")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubWeight {
    @EmbeddedId
    GitHubPK gitHubPK;

    @Column(name="weight")
    private int weight;

    @Column(name ="email")
    private String email;

    public GitHubWeight(){

    }

    public GitHubWeight(GitHubPK gitHubPK, String email, int weight) {
        this.gitHubPK = gitHubPK;
        this.email = email;
        this.weight = weight;
    }

    public GitHubPK getGitHubPK() {
        return gitHubPK;
    }

    public void setGitHubPK(GitHubPK gitHubPK) {
        this.gitHubPK = gitHubPK;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GitHubWeight{" +
                "gitHubPK=" + gitHubPK +
                ", weight=" + weight +
                ", email='" + email + '\'' +
                '}';
    }
}
