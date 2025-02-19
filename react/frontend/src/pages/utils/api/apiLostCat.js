import axios from "axios";
import { backendIP, getJsonHeadersWithJWT } from "../properties";

const sendCatVector = ({  
  vectorMap,
  tempImageURLs,
  suggestedCatBreed,
  suggestedCatName

}) => {
  let payload = {
    vectorMap: vectorMap,
    tempImageURLs: tempImageURLs,
    suggestedCatBreed: suggestedCatBreed,
    suggestedCatName: suggestedCatName
  }
  return axios.post(`${backendIP}/api/get_top_cats`, payload, {
    headers: getJsonHeadersWithJWT(),
  });
}

let getTopCats = (data) => {
  return axios.post(`${backendIP}/api/show_top_cats`, data, {
      headers: getJsonHeadersWithJWT(),
  });
}

export { sendCatVector, getTopCats };
