package nus.iss.team11.model;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CatSighting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	private SCSUser scsUser;

	@ManyToOne
	private Cat cat;

	private String sightingName;
	private Float locationLat;
	private Float locationLong;

	private LocalDate time;

	@OneToMany(mappedBy = "catSighting")

	private List<AzureImage> images;

	private String suggestedCatName;
	private String suggestedCatBreed;

	// for now this value is not used yet.
	// might need this later when we have multiple sightings under same cat.
	private boolean isApproved;

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		json.put("id", id);
		json.put("suggestedCatName", suggestedCatName);
		json.put("suggestedCatBreed", suggestedCatBreed);
		json.put("locationLat", locationLat);
		json.put("locationLong", locationLong);
		json.put("time", time);
		json.put("sightingName", sightingName);
		json.put("scsUser", scsUser.getUsername());
		if (cat != null) {
			json.put("cat", cat.getId());
		}

		JSONArray urls = new JSONArray();
		images.stream().forEach(image -> {
			urls.put(image.getImageURL());
		});
		json.put("imagesURLs", urls);

		// TODO: add this when we have test data for scsUser
		// json.put("uploadedBy", scsUser.getUsername());

		return json;
	}

}
