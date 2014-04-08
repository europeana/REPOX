package harvesterUI.client.panels.harvesting.calendar;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.bradrydzewski.gwt.calendar.client.Attendee;
import com.bradrydzewski.gwt.calendar.client.Attending;
import com.google.gwt.user.client.Random;

import java.util.ArrayList;
import java.util.Date;

/**
* Created to REPOX.
* User: Edmundo
* Date: 23-03-2011
* Time: 15:14
*/

/**
* Utility class to create random sample appointments from hard-coded arrays of
* dummy data.
*
* @author Brad Rydzewski
* @author Carlos D. Morales
*/
public class AppointmentBuilder {
    /**
     * The available styles that can be applied to appointments when using the
     * the &quot;Google&quot; theme.
     */
    public static final AppointmentStyle[] GOOGLE_STYLES =
            new AppointmentStyle[]{AppointmentStyle.GREEN, AppointmentStyle.BLUE,
    	AppointmentStyle.LIGHT_GREEN, AppointmentStyle.BLUE_GREY};

    /**
     * The available styles that can be applied to appointments when using the
     * &quot;iCal&quot; theme.
     */
    public static final AppointmentStyle[] ICAL_STYLES =
            new AppointmentStyle[]{AppointmentStyle.GREEN, AppointmentStyle.BLUE,
    	AppointmentStyle.PURPLE,AppointmentStyle.RED};

    /**
     * Available hours for appointments, from 0 to 24.
     */
    protected static Integer[] HOURS =
            new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                    16, 17, 18, 19, 20, 21, 22, 23, 24};

    /**
     * Available minutes for the appointments, multiples of 15 being used.
     */
    protected static Integer[] MINUTES = new Integer[]{0, 15, 30, 45};

    /**
     * Available durations for the appointments in minutes; from 15 minutes to 4
     * hours.
     */
    protected static Integer[] DURATIONS =
            new Integer[]{15, 30, 45, 60, 90, 120, 180, 240};
    /**
     * The maximum number of appointments to generate per day.
     */
    protected static int appointmentsPerDay = 8;

    /**
     * Available locations for the generated appointments.
     */
    protected static final String[] LOCATIONS =
            new String[]{"Conference Room A", "Conference Room B", "Antarctica",
                    "Scottsdale"};

    /**
     * Sample email addresses for the appointment attendees.
     */
    protected static final String[] EMAIL =
            new String[]{"john.smith@gmail.com", "mike.anderson@gmail.com",
                    "jane.doe@gmail.com", "john.petrucci@gmail.com"};

    /**
     * Available options for attending an appointment.
     */
    protected static final Attending[] ATTENDING =
            new Attending[]{Attending.Yes, Attending.No, Attending.Maybe};

    /**
     * Titles of the generated appointments. Position of each entry in this
     * array corresponds with the same position in the {@link #DESCRIPTIONS}
     * array.
     */
    protected static final String[] TITLES =
            new String[]{"Walk the Dog", "Watch Matlock", "Visit the Doctor",
                    "In-laws coming to visit...run!", "Dinner party at Carol's",
                    "Meal at Planet Pizza", "Takeout from Kit's Thai Kitchen",
                    "Office Happy Hour", "Bowling Night!", "Jimbob's birthday",
                    "Mow the lawn", "<i><b>Vacation</b></i> in Erie, PA",
                    "Happy Hour at Sapporos", "Get oil changed"};

    /**
     * Descriptions for the generated appointments. Position of each entry in
     * this array corresponds with the title in the same position in the {@link
     * #TITLES} array.
     */
    protected static String[] DESCRIPTIONS =
            new String[]{"at the dog park, fetch ball", "<i><b>best</b> show on TV</i>",//<img src='http://t2.gstatic.com/images?q=tbn:iu9uOKIZkgnVxM:http://images.starpulse.com/Photos/Previews/Matlock-tv-01.jpg' />",
                    "Need to examen that rash...",
                    "tell wife I need to 'work late'",
                    "bring famous spicy nacho dip",
                    "best pizza in fairfield county",
                    "perhaps some drunken noodles?",
                    "more non-alcoholic beer... come on!",
                    "five dollar pitchers and disco music, watch out",
                    "don't forget a card", "", "",
                    "watch all the cougars at work",
                    "also ask to check breaks and tires"};

    /**
     * Builds a collection of randomly generated appointments created after the
     * available elements in this builder class.
     *
     * @return A list with random appointments. A maximum of {@link
     *         #appointmentsPerDay} appointments will be generated per day.
     */
    public static ArrayList<Appointment> build() {
        return build(GOOGLE_STYLES);
    }

    /**
     * Generate random Appointments.
     */
    @SuppressWarnings("deprecation")
    public static ArrayList<Appointment> build(AppointmentStyle[] styles) {
        ArrayList<Appointment> list = new ArrayList<Appointment>();

        Date now = new Date();
        now.setHours(0);
        now.setMinutes(0);
        now.setSeconds(0);
        now.setDate(now.getDate());

        for (int day = 0; day < 14; day++) {

            for (int a = 0; a < appointmentsPerDay; a++) {

                Date start = (Date) now.clone();
                int hour = HOURS[Random.nextInt(HOURS.length)];
                int min = MINUTES[Random.nextInt(MINUTES.length)];
                int dur = DURATIONS[Random.nextInt(DURATIONS.length)];
                start.setHours(hour);
                start.setMinutes(min);

                Date end = (Date) start.clone();
                end.setMinutes(start.getMinutes() + dur);

                AppointmentStyle style = styles[Random.nextInt(styles.length)];
                Appointment appt = new Appointment();
                appt.setStart(start);
                appt.setEnd(end);
                appt.setCreatedBy((EMAIL[Random.nextInt(EMAIL.length)]));
                int titleId = Random.nextInt(TITLES.length);
                appt.setTitle(TITLES[titleId]);
                appt.setDescription(DESCRIPTIONS[titleId]);
                appt.setStyle(style);
                appt.setLocation(LOCATIONS[Random.nextInt(LOCATIONS.length)]);
                int attendees = Random.nextInt(EMAIL.length) + 1;
                for (int i = 0; i < attendees; i++) {
                    Attendee attendee = new Attendee();
                    attendee.setEmail(EMAIL[Random.nextInt(EMAIL.length)]);
                    attendee.setAttending(
                            ATTENDING[Random.nextInt(ATTENDING.length)]);
                    appt.getAttendees().add(attendee);
                }

//                if (appt.getStart().getDate() != appt.getEnd().getDate())
//                    appt.setMultiDay(true);

                list.add(appt);
            }

            //increment date by +1
            now.setDate(now.getDate() + 1);
        }
        return list;
    }
}

