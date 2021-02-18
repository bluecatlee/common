package com.github.bluecatlee.common.datasource.dynamic.test;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * Created by 胶布 on 2021/2/18.
 */
@Data
@Table(name = "actor")
public class Actor {

    private Integer actorId;
    private String firstName;
    private String lastName;
    private Date lastUpdate;

}
