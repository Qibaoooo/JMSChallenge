import { uploadSightingPhoto } from "pages/utils/api/apiAzureImage";
import { getVectorsOfImage } from "pages/utils/api/apiMachineLearning";
import { getFileType } from "pages/utils/fileUtil";
import React, { useRef, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { RxClipboard } from "react-icons/rx";

const ImagePicker = ({
  imageURLs,
  setImageURLs,
  requireVectors,
  vectorMap,
  setVectorMap,
  maxImageCount = 0,
}) => {
  const fileInputRef = useRef();
  const [images, setImages] = useState([]);
  const [uploading, SetUploading] = useState(false);

  const shouldDisplayAddButton = () => {
    if (maxImageCount === 0) {
      return true;
    }
    return images.length < maxImageCount;
  };

  const handleChange = (event) => {
    if (event.target.files && event.target.files[0]) {
      const newImageFile = event.target.files[0];

      // filter for file type
      const fileType = getFileType(newImageFile.name);
      if (!(fileType === "png" || fileType === "jpg")) {
        alert("only jpg and png files are supported.");
        return;
      }

      // show mini size images uploaded
      setImages((oldArray) => [...oldArray, URL.createObjectURL(newImageFile)]);

      // start uploading from here
      SetUploading(true);

      // IMPORTANT NOTE:
      // here we do NOT upload directly to the `images` container
      // because the user might close our page before filling up the form
      // and then we might have dirty data in our `images` container
      // So, we upload photos to `temp` container first.
      // When user click [submit form], we rename and move these photos to `images` container.
      // Also just use the current epoch time as the temp file name.
      uploadSightingPhoto(newImageFile, Date.now().toString(), fileType).then(
        (resp) => {
          const tempImageURL = resp.data;
          setImageURLs((oldArray) => [...oldArray, tempImageURL]);

          // only send request to ML api if needed.
          if (requireVectors) {
            getVectorsOfImage(tempImageURL).then((resp) => {
              console.log(resp);
              const newVector = resp.data;
              let newPair = {};
              newPair[tempImageURL] = newVector;
              setVectorMap({
                ...vectorMap,
                ...newPair,
              });

              SetUploading(false);
            });
          } else {
            SetUploading(false);
          }
        }
      );
    }
  };

  return (
    <div>
      <Modal
        show={uploading}
        onHide={() => {
          SetUploading(false);
        }}
        backdrop="static"
        keyboard={false}
      >
        <Modal.Header>
          <Modal.Title>Uploading...</Modal.Title>
        </Modal.Header>
        <Modal.Body></Modal.Body>
      </Modal>

      <p>Click + to add photos</p>

      {images.map((url, index, array) => {
        return (
          <img
            className="m-2"
            style={{ maxWidth: "100px" }}
            key={index}
            src={url}
          ></img>
        );
      })}
      {!!shouldDisplayAddButton() && (
        <Button
          className="mx-3"
          variant="outline-dark"
          onClick={() => fileInputRef.current.click()}
        >
          +
        </Button>
      )}
      <p>
        {imageURLs.map((url, index, array) => {
          return (
            <div style={{ overflow: "scroll", width: "100%" }}>
              <a className="text-info m-2" href={url} key={index}>
                image blob link {index}
              </a>
              <span> - </span>
              <span
                data-toggle="tooltip"
                data-placement="top"
                title={JSON.stringify(vectorMap[url])}
                style={{ cursor: "pointer" }}
                onClick={() => {
                  navigator.clipboard.writeText(vectorMap[url]);
                }}
              >
                <RxClipboard className="mx-1"></RxClipboard>
                img vector
              </span>
            </div>
          );
        })}
      </p>
      <input
        onChange={handleChange}
        multiple={false}
        ref={fileInputRef}
        type="file"
        hidden
      />
    </div>
  );
};

export default ImagePicker;
