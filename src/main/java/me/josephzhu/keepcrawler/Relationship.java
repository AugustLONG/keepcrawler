package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 16/4/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Relationship
{
    public List<People> users = new ArrayList<>();
    public String lastId;
}
