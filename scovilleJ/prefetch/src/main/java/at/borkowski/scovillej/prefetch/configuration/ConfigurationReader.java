package at.borkowski.scovillej.prefetch.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.borkowski.scovillej.prefetch.Request;
import at.borkowski.scovillej.prefetch.configuration.model.Configuration;

public class ConfigurationReader {
   private final BufferedReader input;

   public static final String CMD_END = "end";
   public static final String CMD_REQUEST = "request";
   public static final String CMD_RATE_REAL = "rate-real";
   public static final String CMD_RATE_PREDICTION = "rate-prediction";

   public ConfigurationReader(InputStream input) {
      try {
         this.input = new BufferedReader(new InputStreamReader(input, "UTF8"));
      } catch (UnsupportedEncodingException ueEx) {
         throw new RuntimeException(ueEx);
      }
   }

   public Configuration read() throws IOException, ConfigurationException {
      List<Request> requests = new LinkedList<>();
      Map<Long, Integer> real = new HashMap<>();
      Map<Long, Integer> predicted = new HashMap<>();

      String line;
      long tick = 0, lastTick = -1, end = -1;
      int lineCounter = 0;
      while ((line = this.input.readLine()) != null) {
         lineCounter++;
         line = line.replaceAll("#.*$", "");
         String[] split = line.split("\\s+");
         if (split.length == 0 || split[0].length() == 0)
            continue;

         try {
            tick = Long.parseLong(split[0]);
         } catch (NumberFormatException nfEx) {
            throw new ConfigurationException("could not parse tick number on line " + lineCounter + ": " + split[0], nfEx);
         }
         if (tick < 0)
            throw new ConfigurationException("negative tick on line " + lineCounter + ": " + tick);
         if (tick < lastTick)
            throw new ConfigurationException("ticks out of order on line " + lineCounter + ": " + tick + " < " + lastTick);
         if (end != -1)
            throw new ConfigurationException("line " + lineCounter + ": no events after \"" + CMD_END + "\" are allowed");

         if (split[1].equals(CMD_END))
            if (split.length != 2)
               throw new ConfigurationException("no parameters allowed for \"" + CMD_END + "\"");
            else
               end = tick;
         else if (split[1].equals(CMD_REQUEST))
            requests.add(parseRequest(tick, lineCounter, split));
         else if (split[1].equals(CMD_RATE_REAL))
            parseRate(tick, lineCounter, CMD_RATE_REAL, real, split);
         else if (split[1].equals(CMD_RATE_PREDICTION))
            parseRate(tick, lineCounter, CMD_RATE_PREDICTION, predicted, split);
         else
            throw new ConfigurationException("unknown command: " + split[1]);
         
         lastTick = tick;
      }

      if (end == -1)
         end = tick;

      return new Configuration(end + 1, requests, real, predicted);
   }

   private void parseRate(long tick, int lineCounter, String cmd, Map<Long, Integer> map, String[] split) throws ConfigurationException {
      if (split.length != 3)
         throw new ConfigurationException("line " + lineCounter + ": usage is \"<tick> " + cmd + " <rate>");

      int rate;
      try {
         rate = Integer.parseInt(split[2]);
      } catch (NumberFormatException nfEx) {
         throw new ConfigurationException("could not parse rate on line " + lineCounter + ": " + split[2], nfEx);
      }

      map.put(tick, rate);
   }

   private Request parseRequest(long tick, int lineCounter, String[] split) throws ConfigurationException {
      if (split.length != 4)
         throw new ConfigurationException("line " + lineCounter + ": usage is \"<tick> " + CMD_REQUEST + " <data> <byterate>");

      int data;
      double byterate;

      try {
         data = Integer.parseInt(split[2]);
      } catch (NumberFormatException nfEx) {
         throw new ConfigurationException("could not parse request data length on line " + lineCounter + ": " + split[2], nfEx);
      }
      try {
         byterate = Double.parseDouble(split[3]);
      } catch (NumberFormatException nfEx) {
         throw new ConfigurationException("could not parse request byte rate on line " + lineCounter + ": " + split[3], nfEx);
      }

      return new Request(tick, data, byterate);
   }
}
