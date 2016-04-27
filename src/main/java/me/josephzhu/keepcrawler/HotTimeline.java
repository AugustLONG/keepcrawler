package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by joseph on 16/4/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotTimeline
{
    public String id;
    public Author author;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Author
    {
        public String id;
        public String username;
        public String avatar;
        public String gender;
    }
}
