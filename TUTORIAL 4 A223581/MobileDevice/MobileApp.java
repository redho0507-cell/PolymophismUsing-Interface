public class MobileApp {

    public static void main(String[] args) {
        BasicPhone basic = new BasicPhone("Nokia", "3310");
        basic.setStation("Radio Klasik");
        basic.setFrequency(98.3);

        SmartPhone smart = new SmartPhone("Samsung", "Galaxy S24", "Android");
        smart.setStation("Hot FM");
        smart.setFrequency(97.6);

        MobileDevice[] devices = {basic, smart};

        for (MobileDevice d : devices) {
            d.printDetails();
            System.out.println("Station: " + d.station);
            System.out.println("Frequency: " + d.frequency);
            System.out.println();
        }
    }
}