package com.ctnc;
import java.util.List;
import java.util.Map;
import com.ctnc.shared.Tickets;

public interface SourceService {
	
   Tickets getTickets(long eventId) throws SourceException; 
   
   Map<String, List<Event>> getEventsByCountriesAbbreviation(String[] countriesAbbreviation) throws SourceException;
}
