import java.util.List;

/**
 * 树形数据实体接口
 * @param <E>
 */
public interface TreeEntity<E> {
    String getEntityId();
    String getEntityParentId();
    void setChildList(List<E> childList);
}
