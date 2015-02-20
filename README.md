# scovilleJ

[![Build Status](https://travis-ci.org/michael-borkowski/scovilleJ.svg?branch=master)](https://travis-ci.org/michael-borkowski/scovilleJ)

scovilleJ is a framework for running synchronous simulations in Java.

Its goal is providing a verastile framework. The architecture is as follows:

* Simulation is divided into **ticks**, each tick is divided into **phases** (real time is irrelevant)
* A simulation consists of several **members** (participants)
* Each member gets notified of each tick, and can perform operations taking as much time as it needs
* Members are allowed to communicate with each other in between phases
* Members can report **measurement series** for reporting

scoviellJ itself is deterministic and synchronous in nature and thus produces repeateable and reproducible results, given that all members behave in a deterministic way.

**scovilleJ is still under heavy development.**

## Build

The project is using maven as a built tool, so simply running

    mvn package

is enough to compile, test and package all source code.

## Usage

To be done

## Testing

### Try it!

To be done

### Unit Tests

To be done

## History

- 2015-02-17: Started project development

## Credits

The readme file has been created using the template from [https://gist.github.com/zenorocha/4526327](https://gist.github.com/zenorocha/4526327)

## License

scovilleJ is developed by Michael Borkowski.

scovilleJ is licensed under the [MIT License](http://opensource.org/licenses/MIT).

