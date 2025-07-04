package ${packageName}.${subPackageName};

import org.apache.ibatis.annotations.Mapper;
import ${packageName}.model.entity.${classNamePrefix}DO;
import top.continew.starter.data.mp.base.BaseMapper;

/**
* ${businessName} Mapper
*
* @author ${author}
* @since ${datetime}
*/
@Mapper
public interface ${className} extends BaseMapper<${classNamePrefix}DO> {}