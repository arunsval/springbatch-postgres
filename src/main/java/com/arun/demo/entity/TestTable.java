package com.arun.demo.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "testtable1", schema = "public")
@Data
public class TestTable {
    @Id
    int id;

    @Column
    String name;
}
