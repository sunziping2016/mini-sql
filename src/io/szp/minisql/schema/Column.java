package io.szp.minisql.schema;

import io.szp.minisql.Type;

import java.io.Serializable;

/**
 * 每列的元信息。
 */
public class Column implements Serializable {
    private String name;
    private Type type;
    private boolean is_not_null;
    private boolean is_primary_key;

    public Column(String name, Type type) {
        this(name, type, false, false);
    }
    /**
     * 构造列元数据。
     *
     * @param name 名字
     * @param type 类型
     * @param is_not_null 是否是有NOT NULL约束
     * @param is_primary_key 是否有主键约束
     */
    public Column(String name, Type type, boolean is_not_null, boolean is_primary_key) {
        this.name = name;
        this.type = type;
        this.is_not_null = is_not_null;
        this.is_primary_key = is_primary_key;
        if (is_primary_key)
            this.is_not_null = true;
    }
    /**
     * 获取列名。
     *
     * @return 列名
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取列类型。
     *
     * @return 类型
     */
    public Type getType() {
        return type;
    }
    /**
     * 是否是NOT NULL约束的键
     * @return 是否是
     */
    public boolean isNotNull() {
        return is_not_null;
    }
    /**
     * 是否是主键约束的键
     * @return 是否是
     */
    public boolean isPrimaryKey() {
        return is_primary_key;
    }

    public void setPrimaryKey(boolean is_primary_key) {
        this.is_primary_key = is_primary_key;
        if (is_primary_key)
            this.is_not_null = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Column other = (Column) obj;
        return name.equals(other.name) &&
                type == other.type &&
                is_not_null == other.is_not_null &&
                is_primary_key == other.is_primary_key;
    }
}
