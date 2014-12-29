package provisiontables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "PDetailValueAdder.findAll", query = "select o from PDetailValueAdder o")
})
@Table(name = "P_DETAIL_VALUE_ADDER")
public class PDetailValueAdder implements Serializable {
    @Column(name="ADDER_TYPE", length = 20)
    private String adderType;
    @Column(name="ADDER_VALUE", length = 40)
    private String adderValue;
    private Double cost;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @ManyToOne
    @JoinColumn(name = "P_TECHNICAL_DETAIL_VALUE_ID")
    private PTechnicalDetailValue PTechnicalDetailValue;

    public PDetailValueAdder() {
    }

    public PDetailValueAdder(String adderType, String adderValue, Double cost,
                             String id,
                             PTechnicalDetailValue PTechnicalDetailValue) {
        this.adderType = adderType;
        this.adderValue = adderValue;
        this.cost = cost;
        this.id = id;
        this.PTechnicalDetailValue = PTechnicalDetailValue;
    }

    public String getAdderType() {
        return adderType;
    }

    public void setAdderType(String adderType) {
        this.adderType = adderType;
    }

    public String getAdderValue() {
        return adderValue;
    }

    public void setAdderValue(String adderValue) {
        this.adderValue = adderValue;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public PTechnicalDetailValue getPTechnicalDetailValue() {
        return PTechnicalDetailValue;
    }

    public void setPTechnicalDetailValue(PTechnicalDetailValue PTechnicalDetailValue) {
        this.PTechnicalDetailValue = PTechnicalDetailValue;
    }
}
