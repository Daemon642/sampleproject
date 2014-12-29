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
  @NamedQuery(name = "PBusinessDetailValue.findAll", query = "select o from PBusinessDetailValue o")
})
@Table(name = "P_BUSINESS_DETAIL_VALUE")
public class PBusinessDetailValue implements Serializable {
    private Double cost;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @Column(name="LIST_ORDER")
    private Long listOrder;
    @Column(length = 400)
    private String txt;
    @ManyToOne
    @JoinColumn(name = "P_BUSINESS_DETAIL_ID")
    private PBusinessDetail PBusinessDetail;

    public PBusinessDetailValue() {
    }

    public PBusinessDetailValue(Double cost, String id, Long listOrder,
                                PBusinessDetail PBusinessDetail, String txt) {
        this.cost = cost;
        this.id = id;
        this.listOrder = listOrder;
        this.PBusinessDetail = PBusinessDetail;
        this.txt = txt;
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

    public Long getListOrder() {
        return listOrder;
    }

    public void setListOrder(Long listOrder) {
        this.listOrder = listOrder;
    }


    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public PBusinessDetail getPBusinessDetail() {
        return PBusinessDetail;
    }

    public void setPBusinessDetail(PBusinessDetail PBusinessDetail) {
        this.PBusinessDetail = PBusinessDetail;
    }
}
