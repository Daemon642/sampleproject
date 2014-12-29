package provisiontables;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class JavaServiceFacade {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProvisionTables");

    public JavaServiceFacade() {
    }

    public static void main(String [] args) {
        final JavaServiceFacade javaServiceFacade = new JavaServiceFacade();
        //  TODO:  Call methods on javaServiceFacade here...
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Object queryByRange(String jpqlStmt, int firstResult,
                               int maxResults) {
        Query query = getEntityManager().createQuery(jpqlStmt);
        if (firstResult > 0) {
            query = query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query = query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    private Object _persistEntity(Object entity) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                em.persist(entity);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    entity = null;
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return entity;
    }

    public PTechnicalDetailValue persistPTechnicalDetailValue(PTechnicalDetailValue PTechnicalDetailValue) {
        return (PTechnicalDetailValue)_persistEntity(PTechnicalDetailValue);
    }

    private Object _mergeEntity(Object entity) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                em.merge(entity);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    entity = null;
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return entity;
    }

    public PTechnicalDetailValue mergePTechnicalDetailValue(PTechnicalDetailValue PTechnicalDetailValue) {
        return (PTechnicalDetailValue)_mergeEntity(PTechnicalDetailValue);
    }

    public void removePTechnicalDetailValue(PTechnicalDetailValue PTechnicalDetailValue) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PTechnicalDetailValue = em.find(PTechnicalDetailValue.class, PTechnicalDetailValue.getId());
                em.remove(PTechnicalDetailValue);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PTechnicalDetailValue o</code> */
    public List<PTechnicalDetailValue> getPTechnicalDetailValueFindAll() {
        return getEntityManager().createNamedQuery("PTechnicalDetailValue.findAll").getResultList();
    }

    public PTechnicalDetail persistPTechnicalDetail(PTechnicalDetail PTechnicalDetail) {
        return (PTechnicalDetail)_persistEntity(PTechnicalDetail);
    }

    public PTechnicalDetail mergePTechnicalDetail(PTechnicalDetail PTechnicalDetail) {
        return (PTechnicalDetail)_mergeEntity(PTechnicalDetail);
    }

    public void removePTechnicalDetail(PTechnicalDetail PTechnicalDetail) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PTechnicalDetail = em.find(PTechnicalDetail.class, PTechnicalDetail.getId());
                em.remove(PTechnicalDetail);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PTechnicalDetail o</code> */
    public List<PTechnicalDetail> getPTechnicalDetailFindAll() {
        return getEntityManager().createNamedQuery("PTechnicalDetail.findAll").getResultList();
    }

    public PBusinessDetailValue persistPBusinessDetailValue(PBusinessDetailValue PBusinessDetailValue) {
        return (PBusinessDetailValue)_persistEntity(PBusinessDetailValue);
    }

    public PBusinessDetailValue mergePBusinessDetailValue(PBusinessDetailValue PBusinessDetailValue) {
        return (PBusinessDetailValue)_mergeEntity(PBusinessDetailValue);
    }

    public void removePBusinessDetailValue(PBusinessDetailValue PBusinessDetailValue) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PBusinessDetailValue = em.find(PBusinessDetailValue.class, PBusinessDetailValue.getId());
                em.remove(PBusinessDetailValue);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PBusinessDetailValue o</code> */
    public List<PBusinessDetailValue> getPBusinessDetailValueFindAll() {
        return getEntityManager().createNamedQuery("PBusinessDetailValue.findAll").getResultList();
    }

    public PBusinessParam persistPBusinessParam(PBusinessParam PBusinessParam) {
        return (PBusinessParam)_persistEntity(PBusinessParam);
    }

    public PBusinessParam mergePBusinessParam(PBusinessParam PBusinessParam) {
        return (PBusinessParam)_mergeEntity(PBusinessParam);
    }

    public void removePBusinessParam(PBusinessParam PBusinessParam) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PBusinessParam = em.find(PBusinessParam.class, PBusinessParam.getId());
                em.remove(PBusinessParam);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PBusinessParam o</code> */
    public List<PBusinessParam> getPBusinessParamFindAll() {
        return getEntityManager().createNamedQuery("PBusinessParam.findAll").getResultList();
    }

    public PBusinessDetail persistPBusinessDetail(PBusinessDetail PBusinessDetail) {
        return (PBusinessDetail)_persistEntity(PBusinessDetail);
    }

    public PBusinessDetail mergePBusinessDetail(PBusinessDetail PBusinessDetail) {
        return (PBusinessDetail)_mergeEntity(PBusinessDetail);
    }

    public void removePBusinessDetail(PBusinessDetail PBusinessDetail) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PBusinessDetail = em.find(PBusinessDetail.class, PBusinessDetail.getId());
                em.remove(PBusinessDetail);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PBusinessDetail o</code> */
    public List<PBusinessDetail> getPBusinessDetailFindAll() {
        return getEntityManager().createNamedQuery("PBusinessDetail.findAll").getResultList();
    }

    public PBusinessService persistPBusinessService(PBusinessService PBusinessService) {
        return (PBusinessService)_persistEntity(PBusinessService);
    }

    public PBusinessService mergePBusinessService(PBusinessService PBusinessService) {
        return (PBusinessService)_mergeEntity(PBusinessService);
    }

    public void removePBusinessService(PBusinessService PBusinessService) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PBusinessService = em.find(PBusinessService.class, PBusinessService.getId());
                em.remove(PBusinessService);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PBusinessService o</code> */
    public List<PBusinessService> getPBusinessServiceFindAll() {
        return getEntityManager().createNamedQuery("PBusinessService.findAll").getResultList();
    }

    /** <code>select o from PBusinessService o WHERE o.sQueueId = :sQueueId</code> */
    public PBusinessService getPBusinessServiceFindByQueueId(String sQueueId) {
        List<PBusinessService> result = getEntityManager().createNamedQuery("PBusinessService.findByQueueId").setParameter("sQueueId", sQueueId).getResultList();
        if ( result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /** <code>select o from PBusinessService o WHERE o.sBusinessServiceId = :sBusinessServiceId</code> */
    public PBusinessService getPBusinessServiceFindByBusinessServiceId(String sBusinessServiceId) {
        List<PBusinessService> result = getEntityManager().createNamedQuery("PBusinessService.findByBusinessServiceId").setParameter("sBusinessServiceId", sBusinessServiceId).getResultList();
        if ( result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public PDetailValueAdder persistPDetailValueAdder(PDetailValueAdder PDetailValueAdder) {
        return (PDetailValueAdder)_persistEntity(PDetailValueAdder);
    }

    public PDetailValueAdder mergePDetailValueAdder(PDetailValueAdder PDetailValueAdder) {
        return (PDetailValueAdder)_mergeEntity(PDetailValueAdder);
    }

    public void removePDetailValueAdder(PDetailValueAdder PDetailValueAdder) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PDetailValueAdder = em.find(PDetailValueAdder.class, PDetailValueAdder.getId());
                em.remove(PDetailValueAdder);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PDetailValueAdder o</code> */
    public List<PDetailValueAdder> getPDetailValueAdderFindAll() {
        return getEntityManager().createNamedQuery("PDetailValueAdder.findAll").getResultList();
    }

    public PBusinessValues persistPBusinessValues(PBusinessValues PBusinessValues) {
        return (PBusinessValues)_persistEntity(PBusinessValues);
    }

    public PBusinessValues mergePBusinessValues(PBusinessValues PBusinessValues) {
        return (PBusinessValues)_mergeEntity(PBusinessValues);
    }

    public void removePBusinessValues(PBusinessValues PBusinessValues) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PBusinessValues = em.find(PBusinessValues.class, PBusinessValues.getId());
                em.remove(PBusinessValues);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PBusinessValues o</code> */
    public List<PBusinessValues> getPBusinessValuesFindAll() {
        return getEntityManager().createNamedQuery("PBusinessValues.findAll").getResultList();
    }

    public PTechnicalService persistPTechnicalService(PTechnicalService PTechnicalService) {
        return (PTechnicalService)_persistEntity(PTechnicalService);
    }

    public PTechnicalService mergePTechnicalService(PTechnicalService PTechnicalService) {
        return (PTechnicalService)_mergeEntity(PTechnicalService);
    }

    public void removePTechnicalService(PTechnicalService PTechnicalService) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PTechnicalService = em.find(PTechnicalService.class, PTechnicalService.getId());
                em.remove(PTechnicalService);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PTechnicalService o</code> */
    public List<PTechnicalService> getPTechnicalServiceFindAll() {
        return getEntityManager().createNamedQuery("PTechnicalService.findAll").getResultList();
    }

    public PTechnicalParam persistPTechnicalParam(PTechnicalParam PTechnicalParam) {
        return (PTechnicalParam)_persistEntity(PTechnicalParam);
    }

    public PTechnicalParam mergePTechnicalParam(PTechnicalParam PTechnicalParam) {
        return (PTechnicalParam)_mergeEntity(PTechnicalParam);
    }

    public void removePTechnicalParam(PTechnicalParam PTechnicalParam) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PTechnicalParam = em.find(PTechnicalParam.class, PTechnicalParam.getId());
                em.remove(PTechnicalParam);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PTechnicalParam o</code> */
    public List<PTechnicalParam> getPTechnicalParamFindAll() {
        return getEntityManager().createNamedQuery("PTechnicalParam.findAll").getResultList();
    }

    public PTechnicalValues persistPTechnicalValues(PTechnicalValues PTechnicalValues) {
        return (PTechnicalValues)_persistEntity(PTechnicalValues);
    }

    public PTechnicalValues mergePTechnicalValues(PTechnicalValues PTechnicalValues) {
        return (PTechnicalValues)_mergeEntity(PTechnicalValues);
    }

    public void removePTechnicalValues(PTechnicalValues PTechnicalValues) {
        final EntityManager em = getEntityManager();
        try {
            final EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                PTechnicalValues = em.find(PTechnicalValues.class, PTechnicalValues.getId());
                em.remove(PTechnicalValues);
                et.commit();
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /** <code>select o from PTechnicalValues o</code> */
    public List<PTechnicalValues> getPTechnicalValuesFindAll() {
        return getEntityManager().createNamedQuery("PTechnicalValues.findAll").getResultList();
    }
}
