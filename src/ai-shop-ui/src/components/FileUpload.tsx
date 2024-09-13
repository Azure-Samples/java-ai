import React, { useState } from "react";
import { CloudArrowUpIcon } from "@heroicons/react/24/outline";
import classNames from "classnames";
import axios from "axios";
import { PhotoIcon } from '@heroicons/react/24/solid'

// Extend the Window interface
declare global {
  interface Window {
    _env_: {
      REACT_APP_API_URL: string;
    };
  }
}

const FileUpload = (props: any) => {
  const [fileList, setFileList] = useState<File[] | null>(null);
  const [uploadedFileList, setUploadedFileList] = useState<File[] | null>(null);
  const [shouldHighlight, setShouldHighlight] = useState(false);
  const [uploaded, setUploaded] = useState(false);
  const [progress, setProgress] = useState(0);
  const preventDefaultHandler = (e: React.DragEvent<HTMLElement>) => {
    e.preventDefault();
    e.stopPropagation();
  };


  const handleUpload = async () => {

    //const UPLOAD_URL = "http://localhost:8080/upload";
    // Use the extended Window interface
    if (!window._env_) {
      window._env_ = {
        REACT_APP_API_URL: "http://localhost:8080/upload",
      };
    }
    const UPLOAD_URL = window._env_.REACT_APP_API_URL;

    const data = new FormData();
    for (let file of fileList!) {
      data.append("image", file);
    }
    let result = await axios.post(UPLOAD_URL, data, {
      onUploadProgress(e) {
        const progress = e.progress ?? 0;
        setProgress(progress * 100);
        if (progress * 100 >= 100) {
          
        }
      }
    });

    result = result.data;
    
    setUploaded(true);
    setUploadedFileList(fileList);
    //setFileList(null);
    if (props.setData) {
      props.setData(result);
    }
  };
  const uploading = progress > 0 && progress < 100;
  return (
    <div>
      <div className="mt-2 flex justify-center rounded-lg border border-dashed border-gray-900/25 px-6 py-10" 
        onDragOver={(e) => {
          preventDefaultHandler(e);
          setShouldHighlight(true);
        }}
        onDragEnter={(e) => {
          preventDefaultHandler(e);
          setShouldHighlight(true);
        }}
        onDragLeave={(e) => {
          preventDefaultHandler(e);
          setShouldHighlight(false);
        }}
        onDrop={(e) => {
          preventDefaultHandler(e);
          const files = Array.from(e.dataTransfer.files);
          setFileList(files);
          setShouldHighlight(false);
        }}
      >
        { uploaded }
        { !uploaded && <div className="text-center">
         

          { !fileList ? ( <>
            <PhotoIcon aria-hidden="true" className="mx-auto h-12 w-12 text-gray-300" />
            <div className="mt-4 flex text-sm leading-6 text-gray-600">
              <label
                htmlFor="file-upload"
                className="relative cursor-pointer rounded-md bg-white font-semibold text-indigo-600 focus-within:outline-none focus-within:ring-2 focus-within:ring-indigo-600 focus-within:ring-offset-2 hover:text-indigo-500"
              >
                <span>Upload a file</span>
              </label>
              <p className="pl-1">or drag and drop</p>
            </div>
            <p className="text-xs leading-5 text-gray-600">PNG, JPG, GIF up to 10MB</p>
              <input id="file-upload" name="file-upload" type="file" className="sr-only" multiple onChange={(e) => {
                if (e.target.files) {
                  setFileList(Array.from(e.target.files));
                }
              }} />
              </>
                ) :(
                <>
                  {fileList.map((file, i) => {
                    return <div>
                      <img src={URL.createObjectURL(file)} alt={file.name} className="w-30 h-30" />
                      </div>;
                  })}
                  <div className="flex gap-2 mt-2 justify-center pt-8">
                    <button
                      className={classNames({
                        "bg-violet-500 text-violet-50 px-2 py-1 rounded-md": true,
                        "pointer-events-none opacity-40 w-full": uploading,
                      })}
                      onClick={() => {
                        handleUpload();
                      }}
                    >
                      {uploading
                        ? `Uploading...  ( ${progress.toFixed(2)}% )`
                        : "Upload"}
                    </button>
                    {!uploading && (
                      <button
                        className="border border-violet-500 px-2 py-1 rounded-md"
                        onClick={() => {
                          setFileList(null);
                        }}
                      >
                        Clear
                      </button>
                    )}
                  </div>
                </>
              )}
        </div> }
        {uploaded && uploadedFileList && <div className="flex flex-col items-center">
            {uploadedFileList.map((file, i) => {
              return <div>
                <img src={URL.createObjectURL(file)} alt={file.name} className="h-80" />
                </div>;
            })}
            </div>
          }
      </div> 
    </div>
  );
};

export default FileUpload;


