package entity;

import com.suyigou.pojo.TbSpecification;

import java.io.Serializable;
import java.util.List;

public class Specification implements Serializable {
    private TbSpecification specification;
    private List specificationOptionList;

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
