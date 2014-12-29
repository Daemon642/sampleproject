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
  @NamedQuery(name = "PBusinessValues.findAll", query = "select o from PBusinessValues o")
})
@Table(name = "P_BUSINESS_VALUES")
public class PBusinessValues implements Serializable {
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @Column(length = 100)
    private String name;
    @Column(length = 4000)
    private String val;
    @ManyToOne
    @JoinColumn(name = "P_BUSINESS_SERVICE_ID")
    private PBusinessService PBusinessService;

    public PBusinessValues() {
    }

    public PBusinessValues(String id, String name,
                           PBusinessService PBusinessService,
                           String val) {
        this.id = id;
        this.name = name;
        this.PBusinessService = PBusinessService;
        this.val = val;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public PBusinessService getPBusinessService() {
        return PBusinessService;
    }

    public void setPBusinessService(PBusinessService PBusinessService) {
        this.PBusinessService = PBusinessService;
    }
}
