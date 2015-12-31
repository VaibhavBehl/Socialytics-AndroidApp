package com.vbehl.connections.model;

/**
 * 
 * @author Vaibhav.Behl
 * 
 * similar to twitter4j.Status to have a model to be supplied to the adapter for display on ListActivity.
 * 
 * object of this class represents a SINGLE feed!! (thus will be used in a List ususally)
 * 
 * 
 * 
Types of Page Posts
-------------------

Following are the different types of Page Posts:

Events
Links
Notes
Photos
Milestones
Questions
Status Updates
Videos
Offers


{
      "id": "19292868552_10151257359263553", 
      "from": {
        "category": "Product/service", 
        "name": "Facebook Developers", 
        "id": "19292868552"
      }, 
      "message": "The World Hack winners got a surprise visit with Mark Zuckerberg during their trip to the Facebook headquarters last week.  Read more about the winners: http://bit.ly/XFyi7t", 
      "picture": "http://photos-f.ak.fbcdn.net/hphotos-ak-ash4/205658_10151257358958553_446157386_s.jpg", 
      "link": "https://www.facebook.com/photo.php?fbid=10151257358958553&set=a.441861428552.204523.19292868552&type=1&relevant_count=1", 
      "icon": "http://static.ak.fbcdn.net/rsrc.php/v2/yz/r/StEh3RhPvjk.gif", 
      "privacy": {
        "value": ""
      }, 
      "type": "photo", 
      "status_type": "added_photos", 
      "object_id": "10151257358958553", 
      "created_time": "2013-01-25T18:56:39+0000", 
      "updated_time": "2013-01-25T18:56:39+0000", 
      "shares": {
        "count": 107
      }, 
      "likes": {
        "data": [
          {
            "name": "Luan Nguyen Ngoc", 
            "id": "100004411681916"
          }, 
          {
            "name": "Babitha Banu", 
            "id": "100001346788879"
          }, 
          {
            "name": "Argel Martin", 
            "id": "100000710706457"
          }, 
          {
            "name": "Michael Lawrence Brechin", 
            "id": "709790223"
          }
        ], 
        "count": 1045
      }, 
      "comments": {
        "count": 111
      }
}
    
    
 *
 */
public class FbFeedElement {
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
