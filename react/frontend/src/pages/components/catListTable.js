import { getAllCats } from "pages/utils/api/apiCat";
import { getAllCatSightings } from "pages/utils/api/apiCatSightings";
import { clearUserInfoAndRedirectToLogin } from "pages/utils/userinfo";
import React, { useEffect, useState } from "react";
import { Table, Form } from "react-bootstrap";
import { getUserinfoFromLocal } from "pages/utils/userinfo";

const CatListTable = ({ viewType, filterOwnSightings, setFilterOwnSightings }) => {
  const CatCols = ["", "Cat Name", "Cat breed", "Labels"];
  const SightingCols = ["", "Sighting Name", "Location", "Time"];
  const [tableCols, SetTableCols] = useState(CatCols);
  const [cats, SetCats] = useState([]);
  const [sightings, SetSightings] = useState([]);

  useEffect(() => {
    if (viewType === "cat") {
      getAllCats()
        .then((resp) => {
          // console.log(resp.data);
          SetCats(resp.data);
          SetTableCols(CatCols);
        })
        .catch((e) => {
          clearUserInfoAndRedirectToLogin();
        });
    } else {
      getAllCatSightings()
        .then((resp) => {
          let data = resp.data;
          if(filterOwnSightings == true && getUserinfoFromLocal() !== null){
            data = data.filter(cat => cat.scsUser == getUserinfoFromLocal().username)
          }
          console.log(resp.data);
          
          SetSightings(data);
          SetTableCols(SightingCols);
        })
        .catch((e) => {
          clearUserInfoAndRedirectToLogin();
        });
    }
  }, [viewType,filterOwnSightings]);

  return (
    <>
    {(viewType === "sighting" && getUserinfoFromLocal() !== null) && (
      <Form.Group controlId="filterOwnSightingsCheckbox" className="mb-3">
        <Form.Check 
          type="checkbox"
          label="Filter Own Sightings"
          checked={filterOwnSightings}
          onChange={(e) => setFilterOwnSightings(e.target.checked)}
        />
      </Form.Group>
    )}
    <Table className="my-3" striped bordered={false} hover variant="primary">
      <thead>
        <tr>
          <th>{tableCols[0]}</th>
          <th>{tableCols[1]}</th>
          <th>{tableCols[2]}</th>
          <th>{tableCols[3]}</th>
        </tr>
      </thead>
      <tbody>
        {viewType === "cat"
          ? cats.map((cat, index, array) => {
              return (
                <tr
                  key={index}
                  onClick={() => {
                    window.location.href = `/catDetails?id=${cat.id}`;
                  }}
                >
                  <td>
                    <img
                      src={cat.catSightings[0].imagesURLs[0]}
                      style={{ width: "50px" }}
                    ></img>
                  </td>
                  <td>{cat.catName}</td>
                  <td>{cat.catBreed}</td>
                  <td>{cat.labels.join(", ")}</td>
                </tr>
              );
            })
          : sightings.map((sighting, index, array) => {
              return (
                <tr
                  key={index}
                  onClick={() => {
                    window.location.href = `/catDetails?id=${sighting.cat}`;
                  }}
                >
                  <td>
                    <img
                      src={sighting.imagesURLs[0]}
                      style={{ width: "50px" }}
                    ></img>
                  </td>
                  <td>{sighting.sightingName}</td>
                  <td>{sighting.locationLat + ", " + sighting.locationLong}</td>
                  <td>{sighting.time}</td>
                </tr>
              );
            })}
      </tbody>
    </Table>
    </>
  );
};

export default CatListTable;
