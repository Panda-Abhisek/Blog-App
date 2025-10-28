import React, { useEffect, useRef, useState } from "react";
import { assets, blogCategories } from "../../assets/assets";
import Quill from "quill";
import { option } from "motion/react-client";
import { useAppContext } from "../../context/AppContext";
import toast from "react-hot-toast";

const AddBlog = () => {
  const editorRef = useRef(null);
  const quillRef = useRef(null);

  const { axios } = useAppContext();

  const [image, setImage] = useState(false);
  const [title, setTitle] = useState("");
  const [subTitle, setSubTitle] = useState("");
  const [category, setCategory] = useState("");
  const [published, setPublished] = useState(false);
  const [adding, setAdding] = useState(false);
  const [generating, setGenerating] = useState(false);

  const fileToBase64 = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = reject;
    });
  };

  // console.log(published);

  const onSubmitHandler = async (e) => {
    e.preventDefault();

    const description = quillRef.current.root.innerHTML;
    const currentPublished = published;
    // console.log(published);
    const currentCategory = category;

    const base64Image = image ? await fileToBase64(image) : "";

    const obj = {
      image: base64Image,
      title,
      subTitle,
      description,
      category: currentCategory,
      published: currentPublished, // this now stays in sync perfectly
    };
    // console.log(obj);
    try {
      setAdding(true);
      const res = await axios.post(`/api/blogs`, obj);
      // console.log("Blog added:", res.data);
      toast.success("Blog Added");
      setAdding(false);

      // Reset all fields after success
      setImage(false);
      setTitle("");
      setSubTitle("");
      setCategory("Startup");
      setPublished(false);
      quillRef.current.root.innerHTML = "";
    } catch (err) {
      setAdding(false);
      // console.error("Error:", err);
      toast.error("Error: ", err);
    }
  };

  const generateContent = async () => {
    if (!title.trim() && !subTitle.trim()) {
      toast.error("Please enter title or subtitle first!");
      return;
    }

    try {
      toast.loading("Generating content...");
      setGenerating(true);
      const res = await axios.post("/api/ai/generate-blog", {
        title,
        subTitle,
      });
      let content = res.data.content || "";
      // console.log(content);

      // Clean up extra blank lines, spaces, and useless tags
      content = content
        .replace(/^```html\s*/i, "") // remove starting ```html
        .replace(/```$/i, "") // remove ending ```
        .trim()
        .replace(/(<br\s*\/?>\s*){2,}/gi, "<br/>")
        .replace(/>\s+</g, "><");

      // Insert generated content into Quill editor
      const quill = quillRef.current;
      quill.root.innerHTML = content;

      toast.dismiss();
      setGenerating(false);
      toast.success("Content generated successfully!");
    } catch (err) {
      setGenerating(false)
      toast.dismiss();
      toast.error("Failed to generate content!");
      console.error(err);
    }
  };

  useEffect(() => {
    // Initiate Quill only once
    if (!quillRef.current && editorRef.current) {
      quillRef.current = new Quill(editorRef.current, { theme: "snow" });
    }
  }, []);

  return (
    <form
      onSubmit={onSubmitHandler}
      className="flex-1 bg-blue-50/50 text-gray-600 h-full overflow-scroll"
    >
      <div className="bg-white w-full max-w-3xl p-4 md:p-10 sm:m-10 shadow rounded">
        <p>Upload Thumbnail</p>
        <label htmlFor="image">
          <img
            src={!image ? assets.upload_area : URL.createObjectURL(image)}
            className="mt-2 h-16 rounded cursor-pointer"
            alt=""
          />
          <input
            onChange={(e) => setImage(e.target.files[0])}
            type="file"
            id="image"
            hidden
          />
        </label>

        <p className="mt-4">Blog Title</p>
        <input
          type="text"
          placeholder="Type here"
          required
          className="w-full max-w-lg mt-2 p-2 border border-gray-300 outline-none rounded"
          onChange={(e) => setTitle(e.target.value)}
          value={title}
        />

        <p className="mt-4">Sub Title</p>
        <input
          type="text"
          placeholder="Type here"
          required
          className="w-full max-w-lg mt-2 p-2 border border-gray-300 outline-none rounded"
          onChange={(e) => setSubTitle(e.target.value)}
          value={subTitle}
        />

        <p className="mt-4">Blog Description</p>
        <div className="max-w-lg h-74 pb-16 sm:pb-10 pt-2 relative">
          <div ref={editorRef}></div>
          <button
            className="absolute bottom-1 right-2 ml-2 text-xs text-white bg-black/70 px-4 py-1.5 rounded hover:underline cursor-pointer"
            type="button"
            onClick={generateContent}
            disabled={generating}
          >
            {generating ? "Generating content..." : "Generate with AI"}
          </button>
        </div>

        <p className="mt-4">Blog Category</p>
        <select
          className="mt-2 px-3 border text-gray-500 border-gray-300 outline-none rounded"
          name="category"
          value={category} // <-- this was missing
          onChange={(e) => setCategory(e.target.value)}
        >
          <option value="">Select Category</option>
          {blogCategories.map((item, index) => {
            return (
              <option key={index} value={item}>
                {item}
              </option>
            );
          })}
        </select>

        <div className="flex gap-2 mt-4">
          <p>Publish Now</p>
          <input
            type="checkbox"
            checked={published}
            className="scale-125 cursor-pointer"
            onChange={(e) => setPublished(e.target.checked)}
          />
        </div>

        <button
          type="submit"
          disabled={adding}
          className="mt-8 w-40 h-10 bg-primary text-white rounded cursor-pointer text-sm"
        >
          {adding ? "Adding..." : "Add Blog"}
        </button>
      </div>
    </form>
  );
};

export default AddBlog;
