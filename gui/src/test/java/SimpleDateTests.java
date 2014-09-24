import harvesterUI.server.RepoxServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 13-12-2012
 * Time: 16:53
 */
public class SimpleDateTests {

    @Test
    public void compareClientConversionDates() {
        RepoxServiceImpl repoxService = new RepoxServiceImpl();
        double result = repoxService.getClientTimeInUTC("UTC+3:30");
        Assert.assertTrue(result == 3.5);
        result = repoxService.getClientTimeInUTC("UTC+4");
        Assert.assertTrue(result == 4.0);
        result = repoxService.getClientTimeInUTC("UTC+4");
        Assert.assertTrue(result == 4.0);
        result = repoxService.getClientTimeInUTC("UTC-3");
        Assert.assertTrue(result == -3.0);
        result = repoxService.getClientTimeInUTC("UTC-2:30");
        Assert.assertTrue(result == -2.5);
    }

    @Test
    public void compareServerConversionDates() {
        RepoxServiceImpl repoxService = new RepoxServiceImpl();
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Z");
        String serverTimezone = simpleDateFormat.format(now);

        double result = repoxService.getServerTimeInUTC("+0330");
        Assert.assertTrue(result == 3.5);
        result = repoxService.getServerTimeInUTC("-0330");
        Assert.assertTrue(result == -3.5);
        result = repoxService.getServerTimeInUTC("+0400");
        Assert.assertTrue(result == 4.0);
        result = repoxService.getServerTimeInUTC("+0000");
        Assert.assertTrue(result == 0.0);
        result = repoxService.getServerTimeInUTC("+1100");
        Assert.assertTrue(result == 11.0);
        result = repoxService.getServerTimeInUTC("-0300");
        Assert.assertTrue(result == -3.0);
    }

}
