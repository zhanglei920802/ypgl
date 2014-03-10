package xzd.mobile.android.dal;

import java.util.List;

import xzd.mobile.android.model.Base;

 
/**
 * 数据表操作接口
 * 
 * @author ZhangLei
 * 
 */
public interface DBOperation {
	/* 将指定数据写入数据库 */
	public abstract boolean save(Base enity);

	/* 按照ID进行删除记录 */
	public abstract boolean delete(Integer id);

	/* 更新一条记录 */
	public abstract boolean update(Base enity);

	/* 按照ID查找一条记录 */
	public abstract Base find(Integer id);

	/* 获取某一个数据表中的记录总素 */
	public abstract int getCount();

	/* 查找某一个表中所有记录 */
	public abstract List<Base> FindAll();

	/* 删除某一表中的全部记录 */
	public abstract boolean DeleteAll();

}
