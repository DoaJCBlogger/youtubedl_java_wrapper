# youtubedl_java_wrapper
A YouTube scraping assistant that chooses the best format for each video on an individual basis. "Best format" means the smallest file at the highest resolution. VP9 is often smaller than MP4, but not always. You can also set a maximum resolution, in case a channel or playlist looks best at (for example) 480p but offers up to 1080p.

![Screenshot](https://preview.redd.it/qxxqqa7sk5141.png?width=475&format=png&auto=webp&s=0f0efb536a29afc2b6e4404b62e7f132a361690d)

#### How to use

I recommend running this in NetBeans so you can easily copy-paste the output from its console.

Right now the program is not designed to handle individual videos. To scrape a channel or playlist, paste its URL into the URL field and click Start.

#### Settings

- Get \[thumbnail, description] self-explanatory
- Get info JSON: Downloads a JSON file with metadata such as views, likes, and dislikes.
- Max res: The highest resolution you want the program to consider. Some videos, such as TV shows, only go up to 480p but were uploaded as 1080p. You can save space by setting the highest actual resolution in this field.
- Reject 60 fps: 60 fps takes up a lot more space so you might not want it.
- Reject AV1: AV1 is the newest YouTube codec but it hasn't been optimized yet so I avoid it.
- Force MP4: (not implemented yet) This is for channels with videos mostly before 2013/2014 when VP9 was coming into wide use. In these cases it would be better to download the videos as MP4. Keep this unchecked if most of the videos were uploaded in 2014 or later.
- Preserve Asian titles: I added this when I had to scrape a Korean channel and the filenames were being destroyed. With the box unchecked, the program converts the titles to filenames safe for Windows using a method similar to what 4K Video Downloader uses. With this box checked, it will allow youtube-dl to convert the titles to filenames, preserving Asian characters.

#### Known Issues

- "Force MP4" doesn't do anything yet
- The table at the bottom is not used at the moment. All output is sent to stdout (the command window)
- Some channels and playlists don't work. This happens often with little-known channels. This program depends on reported file sizes and for some reason youtube-dl can't provide that for some videos. In this case it may recommend two audio formats instead of video and audio. As long as it doesn't say something like "-f 139+140" it should be fine. If it does, you'll have to decide on your own if you want MP4 or VP9 and edit the command to match your choice.
- This program has trouble with certain playlists that contain private videos in the middle of the list.


#### Building

This project was built with NetBeans IDE 4.2.
