# Voice Activated App for Medical Algorithms

Medical algorithms, such as decision tree approaches to healthcare management, are useful
tools for standardizing responses to events or emergencies, and help to reduce uncertainty. In
emergency situations, reading a bedhead algorithm can interfere with the management of the
patient. Mobile apps exist for following a decision tree, but still require physical interaction.
This project aims to integrate voice controls into a new general app for medical algorithms.



<p float="left">
<a href="https://ibb.co/1TQTHkx"><img src="https://i.ibb.co/YhQhvxG/onboarding.png" alt="onboarding" border="0" width="256"></a>
<a href="https://ibb.co/djwZ6S9"><img src="https://i.ibb.co/WG4CFQ9/categories.png" alt="categories" border="0" width="256"></a>
<a href="https://ibb.co/BfTx8sj"><img src="https://i.ibb.co/3TSqthW/algorithm-transcript.png" alt="algorithm-transcript" border="0" width="256"></a>
</p>

[DEMO](https://youtu.be/TRLvYmQjjMo)

### Voice Recognition Setup

Enabling voice recognition requires two parts, [Google Cloud Speech to Text](https://cloud.google.com/speech-to-text) or [Amazon Transcribe](https://aws.amazon.com/transcribe/) for voice recognition and [LUIS.ai](https://www.luis.ai/) for Natural Language Processing.

##### Google Cloud Speech to Text

Requires API key obtained from [Google Cloud Speech to Text](https://cloud.google.com/speech-to-text), to be entered in the app itself.

##### Amazon Transcribe 

Requires access key and API key from [AWS](https://aws.amazon.com/transcribe/), to be entered in the app itself. Needs to include a [custom vocabulary](https://aws.amazon.com/blogs/machine-learning/build-a-custom-vocabulary-to-enhance-speech-to-text-transcription-accuracy-with-amazon-transcribe/) named "medical" to enhance recognition of specific keywords such as laryngectomy. Example available (amazontranscribe-customvocab.txt) in the root of this repository.

##### LUIS.ai

The ```MedicAlgo.json``` file available in the root of the repo describes and needs to [imported](https://docs.microsoft.com/en-us/azure/cognitive-services/luis/luis-how-to-manage-versions) into a LUIS.ai app. Requires appId, key and endpoint to be filled in ```src/main/java/com/mbaxajl3/medicalgo/controllers/NLUController.java```





### Tests

Testing is split into two parts, local and instrumented unit tests. The latter automates UI testing and forms the main bulk of unit testing.

To generate code coverage of instrumented unit tests in androidTest, run ```.\gradlew createDebugCoverageReport```

Will take >10mins to run, and may fail sometimes.
