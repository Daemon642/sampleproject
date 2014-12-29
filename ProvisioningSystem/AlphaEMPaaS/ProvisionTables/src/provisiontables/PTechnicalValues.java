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
  @NamedQuery(name = "PTechnicalValues.findAll", query = "select o from PTechnicalValues o")
})
@Table(name = "P_TECHNICAL_VALUES")
public class PTechnicalValues implements Serializable {
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @Column(length = 100)
    private String name;
    @Column(length = 4000)
    private String val;
    @ManyToOne
    @JoinColumn(name = "P_TECHNICAL_SERVICE_ID")
    private PTechnicalService PTechnicalService;

    public PTechnicalValues() {
    }

    public PTechnicalValues(String id, String name,
                            PTechnicalService PTechnicalService,
                            String val) {
        this.id = id;
        this.name = name;
        this.PTechnicalService = PTechnicalService;
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

    public PTechnicalService getPTechnicalService() {
        return PTechnicalService;
    }

    public void setPTechnicalService(PTechnicalService PTechnicalService) {
        this.PTechnicalService = PTechnicalService;
    }
}
