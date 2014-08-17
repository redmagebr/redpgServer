/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kinds;

import java.io.Serializable;

/**
 *
 * @author reddo
 */
public class SheetStyle implements Serializable {
    private Integer id;
    private Boolean publicStyle;
    private Boolean seguro;
    private Integer idJogo;
    private Integer criador;
    private String name; // TEXT
    private String html; // JSON
    private String css; // JSON
    private String beforeProcess; // TEXT
    private String afterProcess; // TEXT

    public Integer getCriador() {
        return criador;
    }

    public void setCriador(int criador) {
        this.criador = criador;
    }

    public Boolean isPublicStyle() {
        return publicStyle;
    }
    
    public Boolean getPublicStyle () {
        return publicStyle;
    }

    public void setPublicStyle(Boolean publicStyle) {
        this.publicStyle = publicStyle;
    }

    public Boolean isSeguro() {
        return seguro;
    }
    
    public Boolean getSeguro () {
        return seguro;
    }

    public void setSeguro(Boolean seguro) {
        this.seguro = seguro;
    }

    public Integer getIdJogo() {
        return idJogo;
    }

    public void setIdJogo(int idJogo) {
        this.idJogo = idJogo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getBeforeProcess() {
        return beforeProcess;
    }

    public void setBeforeProcess(String beforeProcess) {
        this.beforeProcess = beforeProcess;
    }

    public String getAfterProcess() {
        return afterProcess;
    }

    public void setAfterProcess(String afterProcess) {
        this.afterProcess = afterProcess;
    }
}
