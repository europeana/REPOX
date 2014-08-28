package pt.utl.ist.mdr;

/*
import pt.ist.xml.profiler.DataProfile;
import pt.ist.xml.profiler.DataProfiler;
import pt.ist.xml.profiler.ProfileConfig;
import pt.ist.xml.profiler.impl.DataProfilerImpl;
import pt.ist.xml.profiler.impl.DynamicProfiler;
import pt.ist.xml.profiler.impl.StaticProfiler;
import pt.ist.xml.profiler.io.ProfileWriter;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
*/

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 29-01-2011
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 * @deprecated
 */
@Deprecated
public class Profile {

    /*
    todo...
    public static void createProfile4Collection(String inputFilePath, String outputFilePath) throws IOException {
        ProfileConfig config = new ProfileConfig(
				false, false, true, 10, 10,
				new StaticProfiler(), new DynamicProfiler());

		DataProfiler profiler = new DataProfilerImpl(config);
		String path = "/exportedRecords/record/metadata";

        System.out.println("inputFilePath = " + inputFilePath);
        System.out.println("outputFilePath = " + outputFilePath);

		DataProfile report = profiler.profile(new StreamSource(inputFilePath), path);
    	new ProfileWriter().write(report, new File(outputFilePath));
    }


    public static void main(String[] args) throws Exception {
        ProfileConfig config = new ProfileConfig(
				false, false, true, 10, 10,
				new StaticProfiler(), new DynamicProfiler());


		DataProfiler profiler = new DataProfilerImpl(config);
		String source = "D:\\Projectos\\tel\\repoxdata\\repository\\outros\\export\\outros-1.xml";
        source = "D:\\Projectos\\tel\\repoxdata\\repository\\outros\\export\\outros-1.xml";
		String path = "/exportedRecords/record/metadata";
		DataProfile report = profiler.profile(new StreamSource(source), path);
    	//new ProfileWriter().write(report, new File("D:\\Projectos\\tel\\repoxdata\\repository\\outros\\export\\outros-1.report.xml"));
        new ProfileWriter().write(report, new File("D:\\Projectos\\tel\\repoxdata\\repository\\outros\\export\\outros-1.profile.xml"));

	}
	*/
}
