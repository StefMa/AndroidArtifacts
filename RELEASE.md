## How to release

* Checkout the master branch
```
git checkout master
```
* Change version in [`build.gradle.kts`](build.gradle.kts)
* Commit changes 
```
git commit -m "Bump version"
```
* Release to bintray without testing (`master` branch should be tested)
```
./gradlew clean build bintrayUpload -PdryRun=false -x test
 ```
 * Create the tag
 ```
 git tag v[VER.SI.ON]
 ```
 * Push the previous created tag
 ```
  git push origin v[VER.SI.ON]
  ```
 * Create the GitHub release
 * Add version back ([`build.gradle.kts`](build.gradle.kts)) to `-NOSNAPSHOT`
 * Push the master branch
 ```
git push origin master
```
 