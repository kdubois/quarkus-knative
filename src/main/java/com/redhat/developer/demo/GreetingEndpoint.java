package com.redhat.developer.demo;


import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;


@Path("/") 
public class GreetingEndpoint {
    
    private String prefix = "<h1 style='font-size:100px; text-align:center; padding-top:100px'>Hello!!</h1>";
    
    private String HOSTNAME =
       System.getenv().getOrDefault("HOSTNAME", "unknown");

    private int count = 0;

    @GET    
    @Produces(MediaType.TEXT_HTML)
    public String greet() {
        count++;
        return prefix + " " + HOSTNAME + ":" + count + "\n";
    }
    
    @GET
    @Path("/myresources") 
    public String getSystemResources() {
         long memory = Runtime.getRuntime().maxMemory();
         int cores = Runtime.getRuntime().availableProcessors();
         System.out.println("/myresources " + HOSTNAME);
         return 
             " Memory: " + (memory / 1024 / 1024) + "MB" +
             " Cores: " + cores + "CPU\n";
    }
    
    @GET
    @Path("/consume") 
    public String consumeSome() {
        System.out.println("/consume " + HOSTNAME);

        Runtime rt = Runtime.getRuntime();
        StringBuilder sb = new StringBuilder();
        long maxMemory = rt.maxMemory();
        long usedMemory = 0;
        // while usedMemory is less than 80% of Max
        while (((float) usedMemory / maxMemory) < 0.80) {
            sb.append(System.nanoTime() + sb.toString());
            usedMemory = rt.totalMemory();
        }
        String msg = "Allocated about 80% (" + humanReadableByteCount(usedMemory, false) + ") of the max allowed JVM memory size ("
            + humanReadableByteCount(maxMemory, false) + ")";
        System.out.println(msg);
        return msg + "\n";
    }

    @GET
    @Path("/spin")
    public String spinSome(@QueryParam("ms") @DefaultValue("5000") int ms) {
        long sleepTime = ms*1000000L; // convert to nanoseconds
        long startTime = System.nanoTime();
        long iterations = 1;
        StringBuilder sb = new StringBuilder();
        while ((System.nanoTime() - startTime) < sleepTime) {
            iterations++;
            iterations = iterations * iterations;
            sb.append(System.nanoTime());
        }
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        long usedMemory = rt.totalMemory();

        return "Spun for " + ms + " milliseconds. Used " + ((float) usedMemory / maxMemory)*100 + "% of memory";
    }

   public static String humanReadableByteCount(long bytes, boolean si) {
      int unit = si ? 1000 : 1024;
      if (bytes < unit)
        return bytes + " B";
      int exp = (int) (Math.log(bytes) / Math.log(unit));
      String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
      return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);  
   }

}
