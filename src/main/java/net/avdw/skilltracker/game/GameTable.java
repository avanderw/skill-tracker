package net.avdw.skilltracker.game;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

@DatabaseTable(tableName = "Game")
public class GameTable {
    @DatabaseField(generatedId = true)
    private Integer pk;
    @DatabaseField
    private String name;
    @DatabaseField
    private BigDecimal initialMean;
    @DatabaseField
    private BigDecimal initialStandardDeviation;
    @DatabaseField
    private BigDecimal beta;
    @DatabaseField
    private BigDecimal dynamicsFactor;
    @DatabaseField
    private BigDecimal drawProbability;

    public GameTable() {

    }

    public GameTable(String name, double initialMean, double initialStandardDeviation, double beta, double dynamicsFactor, double drawProbability) {
        this.name = name;
        this.initialMean = BigDecimal.valueOf(initialMean);
        this.initialStandardDeviation = BigDecimal.valueOf(initialStandardDeviation);
        this.beta = BigDecimal.valueOf(beta);
        this.dynamicsFactor = BigDecimal.valueOf(dynamicsFactor);
        this.drawProbability = BigDecimal.valueOf(drawProbability);
    }

    public Integer getPk() {

        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getInitialMean() {
        return initialMean;
    }

    public void setInitialMean(BigDecimal initialMean) {
        this.initialMean = initialMean;
    }

    public BigDecimal getInitialStandardDeviation() {
        return initialStandardDeviation;
    }

    public void setInitialStandardDeviation(BigDecimal initialStandardDeviation) {
        this.initialStandardDeviation = initialStandardDeviation;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public BigDecimal getDynamicsFactor() {
        return dynamicsFactor;
    }

    public void setDynamicsFactor(BigDecimal dynamicsFactor) {
        this.dynamicsFactor = dynamicsFactor;
    }

    public BigDecimal getDrawProbability() {
        return drawProbability;
    }

    public void setDrawProbability(BigDecimal drawProbability) {
        this.drawProbability = drawProbability;
    }
}
