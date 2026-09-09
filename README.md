# Archived static sites

This repository archives historic W.I.S.V. Christiaan Huygens committee and
event websites. It serves those otherwise separate sites from one Nginx
container in the cluster, rather than maintaining a container for every
archived hostname.

Each hostname has a directory at the repository root. Nginx selects that
directory from the request's `Host` header, so a request for
`2026.hackdelft.nl` is served from `2026.hackdelft.nl/`.

## Clone with Git LFS

Install [Git LFS](https://git-lfs.com/) before cloning. The archive stores
binary assets such as images, fonts, media, PDFs, and archives in LFS.

```sh
git lfs install
git clone git@github.com:WISVCH/archived-static.git
cd archived-static
git lfs pull
git lfs fsck
```

`git lfs fsck` should complete without errors before changing or deploying the
archive.

## Add a site

1. Obtain the rendered static files (not the source project) and put them in a
   new root directory named exactly after the hostname, for example
   `2027.example.org/`. The directory must contain its `index.html` and all
   assets referenced by it.
2. Confirm that binary assets are covered by [`.gitattributes`](.gitattributes).
3. Stage and commit the new directory:

   ```sh
   git add 2027.example.org
   git lfs status
   git commit -m "feat: archive Example 2027 site"
   ```

4. You can build and smoke-test the image, by sending the intended hostname as the Host
   header:

   ```sh
   docker build -t archived-static:test .
   docker run --rm -p 18080:8080 archived-static:test
   # In another terminal:
   curl -H 'Host: 2027.example.org' http://127.0.0.1:18080/
   ```

5. Push the commit. Git LFS uploads new binary assets during `git push`.

## Hostname aliases

When two hostnames must serve exactly the same static site, use a relative
symbolic link instead of copying a directory:

```sh
ln -s 2026.hackdelft.nl 2026.hackdelft.com
git add 2026.hackdelft.com
```

Git records a symbolic link as a small link entry, and Nginx follows it in this
container configuration. Keep links relative and point them only at another
site directory inside this repository. On filesystems without symlink support
(including some Windows setups), Git may check a link out as a plain text file;
use a symlink-capable checkout when building the container.
