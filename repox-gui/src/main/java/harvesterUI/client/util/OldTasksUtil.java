package harvesterUI.client.util;

import harvesterUI.shared.tasks.OldTaskUI;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 04/10/12
 * Time: 15:19
 */
public class OldTasksUtil {

    public static void sortOldTasks(List<OldTaskUI> oldTasks){
        Collections.sort(oldTasks, new Comparator<OldTaskUI>() {
            public int compare(OldTaskUI p1, OldTaskUI p2) {
                boolean isBefore = p1.getDate().before(p2.getDate());
                if(isBefore)
                    return 0;
                else
                    return 1;
            }
        });
    }
}
