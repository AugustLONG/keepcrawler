package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by joseph on 16/4/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractResponse<T>
{
    public T data;
    public Boolean ok;
    public String errorCode;
    public String now;
    public String version;
}
