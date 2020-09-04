package com.joesoft.joesoftconsulting.utility;

import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Issue;

public class SpinnerResource {
    public static final String[] issue_priorities_spinner = {
            Issue.HIGH,
            Issue.MEDIUM,
            Issue.LOW
    };
    public static final int [] issue_priority_images_spinner = {
            R.drawable.ic_priority_high_24dp,
            R.drawable.ic_priority_medium_24dp,
            R.drawable.ic_priority_low_24dp
    };

    public static final String[] issue_status_spinner = {
            Issue.IN_PROGRESS,
            Issue.DONE,
            Issue.IDLE
    };

    public static final String[] issue_types_spinner = {
            Issue.TASK,
            Issue.BUG,
    };
    public static final int[] issue_type_images_spinner = {
            R.drawable.ic_assignment_24dp,
            R.drawable.ic_bug_report_24dp,
    };
}
