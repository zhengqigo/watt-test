package org.fuelteam.watt.test.parallel;

import java.util.Map;

import com.google.common.collect.Maps;

public class Context {
    
    public int adds;

    public int muls;

    public String concats;

    public Map<String, Object> maps = Maps.newConcurrentMap();

    public int getAdds() {
        return adds;
    }

    public void setAdds(int adds) {
        this.adds = adds;
    }

    public int getMuls() {
        return muls;
    }

    public void setMuls(int muls) {
        this.muls = muls;
    }

    public String getConcats() {
        return concats;
    }

    public void setConcats(String concats) {
        this.concats = concats;
    }

    public Map<String, Object> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, Object> maps) {
        this.maps = maps;
    }
}