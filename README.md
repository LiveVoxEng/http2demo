### Example HTTP/2+TLS App

This app is a simple Spring Boot app implementing HTTP/2 and TLS via
the standard Spring Boot embedded Tomcat server.

This apparently causes a problematic interaction with the AWS ALB
under certain conditions.

## Building

This is a standard Spring Boot app.  You can produce a Docker image
from it using the command `./gradlew bootBuildImage --imageName
http2demo:latest` (or whatever name is appropriate for the registry where
you intend to upload the image).  This has also already been done
within this github project via an Action and the result is available
publicly as `ghcr.io/p120ph37/http2demo:latest`

## Deploying

You'll have to figure out how to deploy this in your particular AWS
environment.  Anywhere that is behind an ALB should work.  The ALB
should be set to present an HTTP/2 listener, and to use the HTTP/2+TLS for
the target-group as well.  The app itself could be run on ECS or an
EC2 instance, as long as it is fronted by an ALB.  The app listens on
port 8443 and speaks HTTPS+HTTP/2.

## Reproducing

The issue occurs when a large multipart POST request is made to this
application through an ALB (but not when the same request is made
directly).  These commands can be used to produce the problematic
behavior:

```
# Check that the ALB is working... (Will reply with the strinf "Ok")
curl -v -k --http2 https://my-alb-name-1234567890.us-east-1.elb.amazonaws.com/

# Produce a test data file full of some random bytes...
dd if=/dev/random of=./100MB.dat bs=1048576 count=100

# Try to upload the data through the ALB to the app...
# (will fail with a 503-Bad-Gateway error)
curl -v -k --http2 https://my-alb-name-1234567890.us-east-1.elb.amazonaws.com/upload -F file=@100MB.dat

# Try the same upload to the instance private IP directly...
# (will succeed)
curl -v -k --http2 https://10.1.2.3:8443/upload -F file=@100MB.dat
```
