package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Specification implements Serializable{

    private TbSpecification TSpecification;
    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getTSpecification() {
        return TSpecification;
    }

    public void setTSpecification(TbSpecification TSpecification) {
        this.TSpecification = TSpecification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
