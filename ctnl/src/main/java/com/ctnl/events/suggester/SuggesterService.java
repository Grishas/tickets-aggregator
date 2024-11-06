package com.ctnl.events.suggester;
import java.util.List;
import java.util.Map;
//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Event;
import com.ctnc.SuggesterDto;

public class SuggesterService 
{
	//private Logger logger = Logger.getLogger(SuggesterService.class);

	@Autowired
	private SuggesterByEventName suggesterByEventName; 
	
	@Autowired
	private SuggesterByVenueName suggesterByVenueName; 

	@Autowired
	private SuggesterByPerformerName suggesterByPerformerName; 
	
	@Autowired
	private SuggesterByLocationName suggesterByLocationName; 
	
    public SuggesterService(){}
    
    public void init(){}
    public void destroy(){}
    
    
    
    
    
    
    public List<SuggesterDto> buildSuggesterByEventName(Map<String, List<Event>> events) throws SuggesterException{   
    	return this.suggesterByEventName.build(events);
    }
    public String buildSuggesterByEventNameSpell(List<SuggesterDto> suggestersByEventName) {
		return this.suggesterByEventName.buildSpell(suggestersByEventName);	
	}
    
    
    
    
    public List<SuggesterDto> buildSuggesterByVenueName(Map<String, List<Event>> events, Map<String, List<String>> identicalVenues) throws SuggesterException{   
    	return this.suggesterByVenueName.build(events,identicalVenues);
    }
    public String buildSuggesterByVenueNameSpell(List<SuggesterDto> suggestersByVenueName) {
		return this.suggesterByVenueName.buildSpell(suggestersByVenueName);
	}
    
    
    
    
    
    
    public List<SuggesterDto> buildSuggesterByPerformerName(Map<String, List<Event>> events) throws SuggesterException{   
    	return this.suggesterByPerformerName.build(events);
    }
    public String buildSuggesterByPerformerNameSpell(List<SuggesterDto> suggestersByPerformerName) {
		return this.suggesterByPerformerName.buildSpell(suggestersByPerformerName);
	}

    
    
    
    
    

	public List<SuggesterDto> buildSuggesterByLocationName(Map<String, List<Event>> events) throws SuggesterException{
		return this.suggesterByLocationName.build(events);
	}
	public String buildSuggesterByLocationNameSpell(List<SuggesterDto> suggestersByLocationName) {
	 	return this.suggesterByLocationName.build(suggestersByLocationName);
	}
}
