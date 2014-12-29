package provisiontables;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "PBusinessDetail.findAll", query = "select o from PBusinessDetail o")
})
@Table(name = "P_BUSINESS_DETAIL")
public class PBusinessDetail implements Serializable {
    @Column(length = 200)
    private String header;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @OneToMany(mappedBy = "PBusinessDetail")
    private List<PBusinessDetailValue> PBusinessDetailValueList;
    @ManyToOne
    @JoinColumn(name = "P_BUSINESS_SERVICE_ID")
    private PBusinessService PBusinessService;

    public PBusinessDetail() {
    }

    public PBusinessDetail(String header, String id,
                           PBusinessService PBusinessService) {
        this.header = header;
        this.id = id;
        this.PBusinessService = PBusinessService;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<PBusinessDetailValue> getPBusinessDetailValueList() {
        return PBusinessDetailValueList;
    }

    public void setPBusinessDetailValueList(List<PBusinessDetailValue> PBusinessDetailValueList) {
        this.PBusinessDetailValueList = PBusinessDetailValueList;
    }

    public PBusinessDetailValue addPBusinessDetailValue(PBusinessDetailValue PBusinessDetailValue) {
        getPBusinessDetailValueList().add(PBusinessDetailValue);
        PBusinessDetailValue.setPBusinessDetail(this);
        return PBusinessDetailValue;
    }

    public PBusinessDetailValue removePBusinessDetailValue(PBusinessDetailValue PBusinessDetailValue) {
        getPBusinessDetailValueList().remove(PBusinessDetailValue);
        PBusinessDetailValue.setPBusinessDetail(null);
        return PBusinessDetailValue;
    }

    public PBusinessService getPBusinessService() {
        return PBusinessService;
    }

    public void setPBusinessService(PBusinessService PBusinessService) {
        this.PBusinessService = PBusinessService;
    }
}
