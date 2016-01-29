package com.tevinjeffrey.njitct.database;

import com.orm.SugarRecord;

public class TrackedSections extends SugarRecord<TrackedSections> {
    private int sectionId;

    public TrackedSections() {
    }

    public TrackedSections(int id) {
        this.sectionId = id;
    }

    public int getSectionId() {
        return sectionId;
    }
}
