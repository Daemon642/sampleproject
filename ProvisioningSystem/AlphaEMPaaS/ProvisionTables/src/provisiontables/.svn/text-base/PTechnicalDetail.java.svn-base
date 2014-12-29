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
  @NamedQuery(name = "PTechnicalDetail.findAll", query = "select o from PTechnicalDetail o")
})
@Table(name = "P_TECHNICAL_DETAIL")
public class PTechnicalDetail implements Serializable {
    @Column(length = 200)
    private String header;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @ManyToOne
    @JoinColumn(name = "P_TECHNICAL_SERVICE_ID")
    private PTechnicalService PTechnicalService;
    @OneToMany(mappedBy = "PTechnicalDetail")
    private List<PTechnicalDetailValue> PTechnicalDetailValueList;

    public PTechnicalDetail() {
    }

    public PTechnicalDetail(String header, String id,
                            PTechnicalService PTechnicalService) {
        this.header = header;
        this.id = id;
        this.PTechnicalService = PTechnicalService;
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


    public PTechnicalService getPTechnicalService() {
        return PTechnicalService;
    }

    public void setPTechnicalService(PTechnicalService PTechnicalService) {
        this.PTechnicalService = PTechnicalService;
    }

    public List<PTechnicalDetailValue> getPTechnicalDetailValueList() {
        return PTechnicalDetailValueList;
    }

    public void setPTechnicalDetailValueList(List<PTechnicalDetailValue> PTechnicalDetailValueList) {
        this.PTechnicalDetailValueList = PTechnicalDetailValueList;
    }

    public PTechnicalDetailValue addPTechnicalDetailValue(PTechnicalDetailValue PTechnicalDetailValue) {
        getPTechnicalDetailValueList().add(PTechnicalDetailValue);
        PTechnicalDetailValue.setPTechnicalDetail(this);
        return PTechnicalDetailValue;
    }

    public PTechnicalDetailValue removePTechnicalDetailValue(PTechnicalDetailValue PTechnicalDetailValue) {
        getPTechnicalDetailValueList().remove(PTechnicalDetailValue);
        PTechnicalDetailValue.setPTechnicalDetail(null);
        return PTechnicalDetailValue;
    }
}
