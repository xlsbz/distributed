package com.pojo.group;

import java.io.Serializable;
import java.util.List;

import com.jd.pojo.TbSpecification;
import com.jd.pojo.TbSpecificationOption;

/**
 * @author ali 创建包装类绑定规格和规格选项表
 */
public class Specification implements Serializable {

	private TbSpecification specification;// 存放规格对象
	private List<TbSpecificationOption> specificationOptionList;// 存放规格列表

	public TbSpecification getSpecification() {
		return specification;
	}

	public void setSpecification(TbSpecification specification) {
		this.specification = specification;
	}

	public List<TbSpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}

	public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}

}
