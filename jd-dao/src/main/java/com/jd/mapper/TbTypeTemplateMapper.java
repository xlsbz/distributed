package com.jd.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jd.pojo.TbTypeTemplate;
import com.jd.pojo.TbTypeTemplateExample;

public interface TbTypeTemplateMapper {
	int countByExample(TbTypeTemplateExample example);

	int deleteByExample(TbTypeTemplateExample example);

	int deleteByPrimaryKey(Long id);

	int insert(TbTypeTemplate record);

	int insertSelective(TbTypeTemplate record);

	List<TbTypeTemplate> selectByExample(TbTypeTemplateExample example);

	TbTypeTemplate selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") TbTypeTemplate record,
			@Param("example") TbTypeTemplateExample example);

	int updateByExample(@Param("record") TbTypeTemplate record, @Param("example") TbTypeTemplateExample example);

	int updateByPrimaryKeySelective(TbTypeTemplate record);

	int updateByPrimaryKey(TbTypeTemplate record);

	/**
	 * @return
	 */
	List<Map> findTemplate();
}