package net.avdw.skilltracker.game;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

@DatabaseTable(tableName = "Game")
public class GameTable {
    public static final String NAME = "Name";

    @DatabaseField
    private BigDecimal beta;
    @DatabaseField
    private BigDecimal drawProbability;
    @DatabaseField
    private BigDecimal dynamicsFactor;
    @DatabaseField
    private BigDecimal initialMean;
    @DatabaseField
    private BigDecimal initialStandardDeviation;
    @DatabaseField
    private String name;
    @DatabaseField(generatedId = true)
    private Integer pk;

    public GameTable() {

    }

    public GameTable(final String name, final double initialMean, final double initialStandardDeviation, final double beta, final double dynamicsFactor, final double drawProbability) {
        this.name = name;
        this.initialMean = BigDecimal.valueOf(initialMean);
        this.initialStandardDeviation = BigDecimal.valueOf(initialStandardDeviation);
        this.beta = BigDecimal.valueOf(beta);
        this.dynamicsFactor = BigDecimal.valueOf(dynamicsFactor);
        this.drawProbability = BigDecimal.valueOf(drawProbability);
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(final BigDecimal beta) {
        this.beta = beta;
    }

    public BigDecimal getDrawProbability() {
        return drawProbability;
    }

    public void setDrawProbability(final BigDecimal drawProbability) {
        this.drawProbability = drawProbability;
    }

    public BigDecimal getDynamicsFactor() {
        return dynamicsFactor;
    }

    public void setDynamicsFactor(final BigDecimal dynamicsFactor) {
        this.dynamicsFactor = dynamicsFactor;
    }

    public BigDecimal getInitialMean() {
        return initialMean;
    }

    public void setInitialMean(final BigDecimal initialMean) {
        this.initialMean = initialMean;
    }

    public BigDecimal getInitialStandardDeviation() {
        return initialStandardDeviation;
    }

    public void setInitialStandardDeviation(final BigDecimal initialStandardDeviation) {
        this.initialStandardDeviation = initialStandardDeviation;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getPk() {

        return pk;
    }

    public void setPk(final Integer pk) {
        this.pk = pk;
    }
}
